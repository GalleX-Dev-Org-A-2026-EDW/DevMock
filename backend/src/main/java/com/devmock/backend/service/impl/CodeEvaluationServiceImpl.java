package com.devmock.backend.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.devmock.backend.dto.CodeEvaluationRequest;
import com.devmock.backend.dto.CodeEvaluationResult;
import com.devmock.backend.service.CodeEvaluationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CodeEvaluationServiceImpl implements CodeEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(CodeEvaluationServiceImpl.class);
    private static final long TIMEOUT_SECONDS = 15;
    private static final int MAX_OUTPUT_SIZE = 100_000;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CodeEvaluationResult evaluate(CodeEvaluationRequest request) {
        String code = request.getCode();
        String expectedAnswer = request.getExpectedAnswer();
        String evalConfig = request.getEvaluationConfig();
        int estimatedTime = request.getEstimatedTimeSeconds() != null ? request.getEstimatedTimeSeconds() : 600;
        int timeUsed = request.getTimeUsedSeconds() != null ? request.getTimeUsedSeconds() : estimatedTime;

        BigDecimal efficiencyScore = BigDecimal.valueOf(
                Math.min(100, Math.max(35, (double) estimatedTime / Math.max(timeUsed, 1) * 80)))
                .setScale(1, RoundingMode.HALF_UP);
        int basePoints = request.getBasePoints() != null ? request.getBasePoints() : 0;

        if (code == null || code.isBlank()) {
            return minimalResult(efficiencyScore, basePoints);
        }

        if (evalConfig == null || evalConfig.isBlank()) {
            return syntaxOnlyResult(code, efficiencyScore, basePoints);
        }

        try {
            JsonNode config = objectMapper.readTree(evalConfig);
            String functionName = config.has("functionName") ? config.get("functionName").asText() : null;
            JsonNode testCases = config.get("testCases");

            if (functionName == null || testCases == null || !testCases.isArray() || testCases.isEmpty()) {
                return syntaxOnlyResult(code, efficiencyScore, basePoints);
            }

            ExecutionResult studentResult = executePython(code, functionName, testCases);
            ExecutionResult expectedResult = executePython(
                    expectedAnswer != null ? expectedAnswer : "", functionName, testCases);

            BigDecimal correctnessScore = calculateCorrectness(studentResult, expectedResult, testCases.size());
            BigDecimal clarityScore = calculateClarity(code);
            BigDecimal logicScore = BigDecimal.valueOf(
                    Math.min(100, correctnessScore.doubleValue() * 0.7 + clarityScore.doubleValue() * 0.3))
                    .setScale(1, RoundingMode.HALF_UP);

            BigDecimal obtainedPoints = calculatePoints(correctnessScore, efficiencyScore, logicScore, clarityScore, basePoints);

            String feedback = buildFeedback(studentResult, correctnessScore, testCases.size());
            String compilationOutput = studentResult.stderr;
            if (compilationOutput != null && compilationOutput.isBlank()) {
                compilationOutput = null;
            }

            return new CodeEvaluationResult(correctnessScore, efficiencyScore, logicScore, clarityScore,
                    obtainedPoints, feedback, compilationOutput);

        } catch (Exception e) {
            log.warn("Code evaluation failed: {}", e.getMessage());
            return syntaxOnlyResult(code, efficiencyScore, basePoints);
        }
    }

    private ExecutionResult executePython(String code, String functionName, JsonNode testCases) throws Exception {
        StringBuilder script = new StringBuilder();
        script.append("import sys\n\n");
        script.append("# === USER CODE ===\n");
        script.append(code).append("\n");
        script.append("# === END USER CODE ===\n\n");

        script.append("test_cases = ").append(testCases.toString()).append("\n");
        script.append("for tc in test_cases:\n");
        script.append("    try:\n");
        script.append("        args = [eval(a) for a in tc['args']]\n");
        script.append("        result = ").append(functionName).append("(*args)\n");
        script.append("        print(result)\n");
        script.append("    except Exception as e:\n");
        script.append("        print(f'ERROR:{e}')\n");

        Path tempDir = Files.createTempDirectory("devmock-");
        Path scriptFile = tempDir.resolve("solution.py");
        Files.writeString(scriptFile, script.toString());

        try {
            ProcessBuilder pb = new ProcessBuilder("python", scriptFile.toAbsolutePath().toString());
            pb.directory(tempDir.toFile());
            pb.redirectErrorStream(false);
            Process process = pb.start();

            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new ExecutionResult("", "TIMEOUT: Excedió " + TIMEOUT_SECONDS + " segundos", false);
            }

            String stdout = new String(process.getInputStream().readAllBytes(), "UTF-8");
            if (stdout.length() > MAX_OUTPUT_SIZE) {
                stdout = stdout.substring(0, MAX_OUTPUT_SIZE);
            }
            String stderr = new String(process.getErrorStream().readAllBytes(), "UTF-8");
            if (stderr.length() > MAX_OUTPUT_SIZE) {
                stderr = stderr.substring(0, MAX_OUTPUT_SIZE);
            }
            boolean success = process.exitValue() == 0;

            return new ExecutionResult(stdout, stderr, success);

        } finally {
            cleanup(tempDir, scriptFile);
        }
    }

    private ExecutionResult checkPythonSyntax(String code) throws Exception {
        // Use ast.parse to check Python syntax without executing
        String script = "import sys, ast\n" +
                "with open(sys.argv[1]) as f:\n" +
                "    try:\n" +
                "        ast.parse(f.read())\n" +
                "        print('OK')\n" +
                "    except SyntaxError as e:\n" +
                "        print(f'SYNTAX_ERROR:{e}')\n";

        Path tempDir = Files.createTempDirectory("devmock-");
        Path scriptFile = tempDir.resolve("syntax_check.py");
        Path codeFile = tempDir.resolve("user_code.py");
        Files.writeString(scriptFile, script);
        Files.writeString(codeFile, code);

        try {
            ProcessBuilder pb = new ProcessBuilder("python", scriptFile.toAbsolutePath().toString(),
                    codeFile.toAbsolutePath().toString());
            pb.directory(tempDir.toFile());
            Process process = pb.start();

            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new ExecutionResult("", "TIMEOUT", false);
            }

            String stdout = new String(process.getInputStream().readAllBytes(), "UTF-8");
            String stderr = new String(process.getErrorStream().readAllBytes(), "UTF-8");
            boolean success = stdout.trim().equals("OK");

            return new ExecutionResult(stdout, stderr, success);

        } finally {
            cleanup(tempDir, scriptFile);
            try { Files.deleteIfExists(codeFile); } catch (IOException ignored) {}
        }
    }

    private CodeEvaluationResult syntaxOnlyResult(String code, BigDecimal efficiencyScore, int basePoints) {
        try {
            ExecutionResult syntaxResult = checkPythonSyntax(code);
            BigDecimal clarityScore = calculateClarity(code);
            BigDecimal correctnessScore;
            String feedback;

            if (syntaxResult.success) {
                correctnessScore = BigDecimal.valueOf(60).setScale(1, RoundingMode.HALF_UP);
                feedback = "Código sintácticamente correcto. Sintaxis válida.";
            } else {
                correctnessScore = BigDecimal.valueOf(20).setScale(1, RoundingMode.HALF_UP);
                String err = syntaxResult.stdout;
                if (err != null && err.length() > 200) err = err.substring(0, 200) + "...";
                feedback = "Error de sintaxis en el código.";
            }

            BigDecimal logicScore = BigDecimal.valueOf(
                    Math.min(100, correctnessScore.doubleValue() * 0.7 + clarityScore.doubleValue() * 0.3))
                    .setScale(1, RoundingMode.HALF_UP);
            BigDecimal obtainedPoints = calculatePoints(correctnessScore, efficiencyScore, logicScore, clarityScore, basePoints);

            String errOutput = syntaxResult.success ? null : syntaxResult.stdout;

            return new CodeEvaluationResult(correctnessScore, efficiencyScore, logicScore, clarityScore,
                    obtainedPoints, feedback, errOutput);

        } catch (Exception e) {
            log.warn("Syntax check failed: {}", e.getMessage());
            return minimalResult(efficiencyScore, basePoints);
        }
    }

    private BigDecimal calculateCorrectness(ExecutionResult student, ExecutionResult expected, int totalTests) {
        if (!student.success || student.stdout == null || student.stdout.isBlank()) {
            if (student.stderr != null && !student.stderr.isBlank()) {
                return BigDecimal.valueOf(10).setScale(1, RoundingMode.HALF_UP);
            }
            return BigDecimal.valueOf(20).setScale(1, RoundingMode.HALF_UP);
        }

        String[] studentLines = student.stdout.trim().split("\n");
        String[] expectedLines = expected.stdout != null ? expected.stdout.trim().split("\n") : new String[0];

        int matches = 0;
        int total = Math.min(studentLines.length, Math.min(expectedLines.length, totalTests));
        if (total == 0) {
            return BigDecimal.valueOf(50).setScale(1, RoundingMode.HALF_UP);
        }

        for (int i = 0; i < total; i++) {
            if (studentLines[i].trim().equals(expectedLines[i].trim())) {
                matches++;
            }
        }

        double score = (double) matches / total * 100;
        if (score < 20) {
            score = 20;
        }
        return BigDecimal.valueOf(score).setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateClarity(String code) {
        if (code == null || code.isBlank()) return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        int lines = code.split("\n").length;
        double score = lines <= 30 ? 100 : Math.max(35, 100 - (lines - 30) * 2);
        return BigDecimal.valueOf(score).setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePoints(BigDecimal correctness, BigDecimal efficiency,
            BigDecimal logic, BigDecimal clarity, int basePoints) {
        double weighted = correctness.doubleValue() * 0.4
                + efficiency.doubleValue() * 0.25
                + logic.doubleValue() * 0.15
                + clarity.doubleValue() * 0.2;
        double points = Math.round(basePoints * weighted / 100);
        return BigDecimal.valueOf(points);
    }

    private CodeEvaluationResult minimalResult(BigDecimal efficiencyScore, int basePoints) {
        BigDecimal zero = BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        return new CodeEvaluationResult(zero, efficiencyScore, zero, zero,
                BigDecimal.ZERO, "No se registró una respuesta evaluable.", null);
    }

    private String buildFeedback(ExecutionResult result, BigDecimal correctness, int totalTests) {
        if (!result.success) {
            String err = result.stderr;
            if (err != null && err.length() > 300) {
                err = err.substring(0, 300) + "...";
            }
            if (err != null && !err.isBlank()) {
                return "Error de ejecución: " + err;
            }
            return "El código no pudo ejecutarse correctamente.";
        }
        if (correctness.doubleValue() >= 90) {
            return "Todos los casos de prueba pasaron. Solución correcta.";
        } else if (correctness.doubleValue() >= 60) {
            return "La mayoría de los casos de prueba pasaron. Revisa tu lógica para los casos fallidos.";
        } else if (correctness.doubleValue() >= 30) {
            return "Algunos casos de prueba pasaron. Revisa la lógica de tu solución.";
        } else {
            return "La mayoría de los casos de prueba fallaron. Revisa el planteamiento de tu solución.";
        }
    }

    private void cleanup(Path dir, Path file) {
        try { Files.deleteIfExists(file); } catch (IOException ignored) {}
        try { Files.deleteIfExists(dir); } catch (IOException ignored) {}
    }

    private static class ExecutionResult {
        final String stdout;
        final String stderr;
        final boolean success;

        ExecutionResult(String stdout, String stderr, boolean success) {
            this.stdout = stdout;
            this.stderr = stderr;
            this.success = success;
        }
    }
}

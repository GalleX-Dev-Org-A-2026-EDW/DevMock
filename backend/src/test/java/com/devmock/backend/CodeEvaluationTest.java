package com.devmock.backend;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.devmock.backend.dto.CodeEvaluationRequest;
import com.devmock.backend.dto.CodeEvaluationResult;
import com.devmock.backend.service.impl.CodeEvaluationServiceImpl;

class CodeEvaluationTest {

    private final CodeEvaluationServiceImpl service = new CodeEvaluationServiceImpl();

    private static final String PALINDROME_CODE = "def is_palindrome(s):\n    cleaned = ''.join(c.lower() for c in s if c.isalnum())\n    return cleaned == cleaned[::-1]";

    private static final String PALINDROME_CONFIG = "{\"language\":\"python\",\"functionName\":\"is_palindrome\",\"testCases\":[{\"args\":[\"\\\"racecar\\\"\"],\"expected\":\"True\"},{\"args\":[\"\\\"hello\\\"\"],\"expected\":\"False\"},{\"args\":[\"\\\"A man a plan a canal Panama\\\"\"],\"expected\":\"True\"}]}";

    @Test
    void evaluateCorrectPalindromeCode() {
        CodeEvaluationRequest req = new CodeEvaluationRequest();
        req.setCode(PALINDROME_CODE);
        req.setExpectedAnswer(PALINDROME_CODE);
        req.setEvaluationConfig(PALINDROME_CONFIG);
        req.setEstimatedTimeSeconds(600);
        req.setTimeUsedSeconds(300);
        req.setBasePoints(50);

        CodeEvaluationResult result = service.evaluate(req);

        assertEquals(100.0, result.getCorrectnessScore().doubleValue(), 0.1,
                "Correct palindrome code should get 100% correctness");
        assertTrue(result.getObtainedPoints().doubleValue() > 0,
                "Should earn points for correct code");
        assertNotNull(result.getEvaluationFeedback());
    }

    @Test
    void evaluateBlankCode() {
        CodeEvaluationRequest req = new CodeEvaluationRequest();
        req.setCode("");
        req.setExpectedAnswer(PALINDROME_CODE);
        req.setEvaluationConfig(PALINDROME_CONFIG);
        req.setEstimatedTimeSeconds(600);
        req.setTimeUsedSeconds(300);
        req.setBasePoints(50);

        CodeEvaluationResult result = service.evaluate(req);

        assertEquals(0, result.getCorrectnessScore().doubleValue(), 0.1,
                "Blank code should get 0 correctness");
        assertEquals(0, result.getObtainedPoints().doubleValue(),
                "Blank code should earn 0 points");
    }

    @Test
    void evaluateWithNewlinesInCode() {
        String codeWithExtraNewlines = "\ndef is_palindrome(s):\n    cleaned = ''.join(c.lower() for c in s if c.isalnum())\n    return cleaned == cleaned[::-1]\n";
        CodeEvaluationRequest req = new CodeEvaluationRequest();
        req.setCode(codeWithExtraNewlines);
        req.setExpectedAnswer(PALINDROME_CODE);
        req.setEvaluationConfig(PALINDROME_CONFIG);
        req.setEstimatedTimeSeconds(600);
        req.setTimeUsedSeconds(300);
        req.setBasePoints(50);

        CodeEvaluationResult result = service.evaluate(req);

        assertEquals(100.0, result.getCorrectnessScore().doubleValue(), 0.1,
                "Code with extra newlines should still be correct");
    }

    @Test
    void evaluateWithCrLfInCode() {
        String codeWithCrLf = "def is_palindrome(s):\r\n    cleaned = ''.join(c.lower() for c in s if c.isalnum())\r\n    return cleaned == cleaned[::-1]";
        CodeEvaluationRequest req = new CodeEvaluationRequest();
        req.setCode(codeWithCrLf);
        req.setExpectedAnswer(PALINDROME_CODE);
        req.setEvaluationConfig(PALINDROME_CONFIG);
        req.setEstimatedTimeSeconds(600);
        req.setTimeUsedSeconds(300);
        req.setBasePoints(50);

        CodeEvaluationResult result = service.evaluate(req);

        assertEquals(100.0, result.getCorrectnessScore().doubleValue(), 0.1,
                "Code with CRLF line endings should still be correct");
    }
}

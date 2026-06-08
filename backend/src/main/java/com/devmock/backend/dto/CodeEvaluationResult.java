package com.devmock.backend.dto;

import java.math.BigDecimal;

public class CodeEvaluationResult {

    private BigDecimal correctnessScore;
    private BigDecimal efficiencyScore;
    private BigDecimal logicScore;
    private BigDecimal clarityScore;
    private BigDecimal obtainedPoints;
    private String evaluationFeedback;
    private String compilationOutput;

    public CodeEvaluationResult() {}

    public CodeEvaluationResult(BigDecimal correctnessScore, BigDecimal efficiencyScore,
            BigDecimal logicScore, BigDecimal clarityScore, BigDecimal obtainedPoints,
            String evaluationFeedback, String compilationOutput) {
        this.correctnessScore = correctnessScore;
        this.efficiencyScore = efficiencyScore;
        this.logicScore = logicScore;
        this.clarityScore = clarityScore;
        this.obtainedPoints = obtainedPoints;
        this.evaluationFeedback = evaluationFeedback;
        this.compilationOutput = compilationOutput;
    }

    public BigDecimal getCorrectnessScore() { return correctnessScore; }
    public void setCorrectnessScore(BigDecimal correctnessScore) { this.correctnessScore = correctnessScore; }

    public BigDecimal getEfficiencyScore() { return efficiencyScore; }
    public void setEfficiencyScore(BigDecimal efficiencyScore) { this.efficiencyScore = efficiencyScore; }

    public BigDecimal getLogicScore() { return logicScore; }
    public void setLogicScore(BigDecimal logicScore) { this.logicScore = logicScore; }

    public BigDecimal getClarityScore() { return clarityScore; }
    public void setClarityScore(BigDecimal clarityScore) { this.clarityScore = clarityScore; }

    public BigDecimal getObtainedPoints() { return obtainedPoints; }
    public void setObtainedPoints(BigDecimal obtainedPoints) { this.obtainedPoints = obtainedPoints; }

    public String getEvaluationFeedback() { return evaluationFeedback; }
    public void setEvaluationFeedback(String evaluationFeedback) { this.evaluationFeedback = evaluationFeedback; }

    public String getCompilationOutput() { return compilationOutput; }
    public void setCompilationOutput(String compilationOutput) { this.compilationOutput = compilationOutput; }
}

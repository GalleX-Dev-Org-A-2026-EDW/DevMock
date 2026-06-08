package com.devmock.backend.dto;

public class CodeEvaluationRequest {

    private String code;
    private String expectedAnswer;
    private String evaluationConfig;
    private Integer estimatedTimeSeconds;
    private Integer timeUsedSeconds;
    private Integer basePoints;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getExpectedAnswer() { return expectedAnswer; }
    public void setExpectedAnswer(String expectedAnswer) { this.expectedAnswer = expectedAnswer; }

    public String getEvaluationConfig() { return evaluationConfig; }
    public void setEvaluationConfig(String evaluationConfig) { this.evaluationConfig = evaluationConfig; }

    public Integer getEstimatedTimeSeconds() { return estimatedTimeSeconds; }
    public void setEstimatedTimeSeconds(Integer estimatedTimeSeconds) { this.estimatedTimeSeconds = estimatedTimeSeconds; }

    public Integer getTimeUsedSeconds() { return timeUsedSeconds; }
    public void setTimeUsedSeconds(Integer timeUsedSeconds) { this.timeUsedSeconds = timeUsedSeconds; }

    public Integer getBasePoints() { return basePoints; }
    public void setBasePoints(Integer basePoints) { this.basePoints = basePoints; }
}

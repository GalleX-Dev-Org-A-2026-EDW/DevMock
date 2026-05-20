package com.devmock.backend.dto;

import java.util.List;

import com.devmock.backend.entity.en_enum.AnswerFormat;
import com.devmock.backend.entity.en_enum.QuestionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class CreateQuestionRequest {

    @NotNull(message = "questionType is required")
    private QuestionType questionType;

    @NotNull(message = "answerFormat is required")
    private AnswerFormat answerFormat;

    @NotBlank(message = "statement is required")
    private String statement;

    private String expectedAnswer;

    private String explanation;

    @PositiveOrZero(message = "estimatedTimeSeconds must be zero or positive")
    private Integer estimatedTimeSeconds;

    @NotNull(message = "basePoints is required")
    @PositiveOrZero(message = "basePoints must be zero or positive")
    private Integer basePoints;

    private String evaluationConfig;

    private List<String> tags;

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public AnswerFormat getAnswerFormat() {
        return answerFormat;
    }

    public void setAnswerFormat(AnswerFormat answerFormat) {
        this.answerFormat = answerFormat;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getExpectedAnswer() {
        return expectedAnswer;
    }

    public void setExpectedAnswer(String expectedAnswer) {
        this.expectedAnswer = expectedAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Integer getEstimatedTimeSeconds() {
        return estimatedTimeSeconds;
    }

    public void setEstimatedTimeSeconds(Integer estimatedTimeSeconds) {
        this.estimatedTimeSeconds = estimatedTimeSeconds;
    }

    public Integer getBasePoints() {
        return basePoints;
    }

    public void setBasePoints(Integer basePoints) {
        this.basePoints = basePoints;
    }

    public String getEvaluationConfig() {
        return evaluationConfig;
    }

    public void setEvaluationConfig(String evaluationConfig) {
        this.evaluationConfig = evaluationConfig;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

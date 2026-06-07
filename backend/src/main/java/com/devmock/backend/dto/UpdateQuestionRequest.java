package com.devmock.backend.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.devmock.backend.entity.en_enum.AnswerFormat;
import com.devmock.backend.entity.en_enum.QuestionType;

public class UpdateQuestionRequest {

    private QuestionType questionType;
    private AnswerFormat answerFormat;
    private String statement;
    private String expectedAnswer;
    private String explanation;
    private Integer estimatedTimeSeconds;
    private Integer basePoints;
    private String evaluationConfig;
    private List<String> tags;
    private Boolean isActive;
    private UUID createdById;
    private UUID categoryId;
    private UUID difficultyId;

    private List<AnswerOptionDto> answerOptions = new ArrayList<>();

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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public UUID getCreatedById() {
        return createdById;
    }

    public void setCreatedById(UUID createdById) {
        this.createdById = createdById;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public UUID getDifficultyId() {
        return difficultyId;
    }

    public void setDifficultyId(UUID difficultyId) {
        this.difficultyId = difficultyId;
    }

    public List<AnswerOptionDto> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<AnswerOptionDto> answerOptions) {
        this.answerOptions = answerOptions;
    }
}

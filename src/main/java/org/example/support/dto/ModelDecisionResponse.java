package org.example.support.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelDecisionResponse {

    private String processInstanceId;
    private String priority;
    private String category;
    private String urgency;

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    private double confidence;
    private String sentiment;
    private Integer expectedResolutionHours;
    private String recommendedAction;

    public Boolean getRequiresHumanReview() {
        return requiresHumanReview;
    }

    public void setRequiresHumanReview(Boolean requiresHumanReview) {
        this.requiresHumanReview = requiresHumanReview;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }

    public Integer getExpectedResolutionHours() {
        return expectedResolutionHours;
    }

    public void setExpectedResolutionHours(Integer expectedResolutionHours) {
        this.expectedResolutionHours = expectedResolutionHours;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    private Boolean requiresHumanReview;


    // 🔥 ADD THIS
    private String escalationLevel;

    // getters & setters
    public String getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(String escalationLevel) {
        this.escalationLevel = escalationLevel;
    }
}

package org.example.support.dto;

public class ModelDecisionResponse {
    private String priority;
    private Boolean shouldCallAgain;
    private String externalProcessId; // optional if Python returns an id for future calls

    public ModelDecisionResponse() {}

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Boolean getShouldCallAgain() { return shouldCallAgain; }
    public void setShouldCallAgain(Boolean shouldCallAgain) { this.shouldCallAgain = shouldCallAgain; }

    public String getExternalProcessId() { return externalProcessId; }
    public void setExternalProcessId(String externalProcessId) { this.externalProcessId = externalProcessId; }
}


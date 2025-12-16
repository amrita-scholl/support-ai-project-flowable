package org.example.support.dto;

public class HumanReviewRequest {
    private boolean approved;
    private String notes;

    public HumanReviewRequest() {}

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

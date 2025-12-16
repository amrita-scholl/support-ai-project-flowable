package org.example.support.dto;

public class TicketRequest {
    private String ticket;
    private Double confidence;

    public TicketRequest() {}

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}

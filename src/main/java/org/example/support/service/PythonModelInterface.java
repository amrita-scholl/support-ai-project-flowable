package org.example.support.service;

import org.example.support.dto.ModelDecisionResponse;
import java.util.Map;

public interface PythonModelInterface {

    /**
     * Sends ticket data to Python API and returns the AI decision.
     *
     * @param payload Map containing:
     *                - "ticket" (String, required)
     *                - "processInstanceId" (String, required)
     *                - "confidence" (Double, optional)
     * @return ModelDecisionResponse
     */
    ModelDecisionResponse analyzeTicket(Map<String, Object> payload);
}


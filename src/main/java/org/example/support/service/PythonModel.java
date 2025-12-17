package org.example.support.service;

import org.example.support.dto.ModelDecisionResponse;

public interface PythonModel {
    ModelDecisionResponse analyzeTicket(
            String ticket,
            String processInstanceId,
            Double confidence
    );
}

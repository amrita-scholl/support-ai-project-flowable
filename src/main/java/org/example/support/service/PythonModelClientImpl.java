package org.example.support.service;

import org.example.support.dto.ModelDecisionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PythonModelClientImpl implements PythonModel, PythonModelInterface {

    private final RestTemplate restTemplate = new RestTemplate();

    public ModelDecisionResponse analyzeTicket(
            String ticket,
            String processInstanceId,
            Double confidence
    ) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("ticket", ticket);
        payload.put("processInstanceId", processInstanceId);

        if (confidence != null) {
            payload.put("confidence", confidence);
        }

        ResponseEntity<ModelDecisionResponse> response =
                restTemplate.postForEntity(
                        "http://localhost:5000/predict",
                        payload,
                        ModelDecisionResponse.class
                );

        return response.getBody();
    }

    @Override
    public ModelDecisionResponse analyzeTicket(Map<String, Object> payload) {
        ResponseEntity<ModelDecisionResponse> response =
                restTemplate.postForEntity(
                        "http://localhost:5000/predict",
                        payload,
                        ModelDecisionResponse.class
                );

        return response.getBody();
    }
}

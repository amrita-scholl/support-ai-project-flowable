package org.example.support.service;

import org.example.support.dto.ModelDecisionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PythonModelClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${python.ai.url:http://localhost:5000/prioritize}")
    private String pythonAiUrl;

    public ModelDecisionResponse requestPriority(
            String ticket,
            Double confidence,
            String processInstanceId) {

        Map<String, Object> payload = new HashMap<>();

        // ✅ FIX: Python expects "ticket"
        payload.put("ticket", ticket);

        payload.put("confidence", confidence);
        payload.put("processInstanceId", processInstanceId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(payload, headers);

        ResponseEntity<ModelDecisionResponse> response =
                restTemplate.postForEntity(
                        pythonAiUrl,
                        request,
                        ModelDecisionResponse.class
                );

        return response.getBody();
    }
}

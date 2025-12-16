package org.example.support.service;

import org.example.support.dto.ModelDecisionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PythonModelClient {

    private final RestTemplate restTemplate;
    private final String modelApiUrl;

    public PythonModelClient(RestTemplate restTemplate,
                             @Value("${model.api.url}") String modelApiUrl) {
        this.restTemplate = restTemplate;
        this.modelApiUrl = modelApiUrl;
    }

    public ModelDecisionResponse requestPriority(String ticketText, Double confidence, String processInstanceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("ticket", ticketText);
        payload.put("confidence", confidence);
        payload.put("processInstanceId", processInstanceId);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        // POST to configured Python endpoint (e.g. POST {modelApiUrl}/prioritize)
        return restTemplate.postForObject(modelApiUrl + "/prioritize", request, ModelDecisionResponse.class);
    }
}


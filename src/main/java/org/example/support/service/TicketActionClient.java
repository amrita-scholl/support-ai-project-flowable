package org.example.support.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TicketActionClient {

    private static final Logger logger = LoggerFactory.getLogger(TicketActionClient.class);

    private final RestTemplate restTemplate = new RestTemplate();

    public void escalateTicket(String processInstanceId) {
        logger.info("Escalating ticket {}", processInstanceId);
        restTemplate.postForObject(
                "http://localhost:8081/escalations",
                buildPayload(processInstanceId),
                Void.class
        );
    }

    public void assignStandardSupport(String processInstanceId) {
        logger.info("Assigning standard support for {}", processInstanceId);
        restTemplate.postForObject(
                "http://localhost:8082/support",
                buildPayload(processInstanceId),
                Void.class
        );
    }

    public void autoResolve(String processInstanceId) {
        logger.info("Auto-resolving ticket {}", processInstanceId);
        restTemplate.postForObject(
                "http://localhost:8083/auto-resolve",
                buildPayload(processInstanceId),
                Void.class
        );
    }

    private Object buildPayload(String processInstanceId) {
        return new Object() {
            public final String processId = processInstanceId;
        };
    }
}

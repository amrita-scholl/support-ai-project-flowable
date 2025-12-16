package org.example.support.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AIClient {

    @Value("${openai.api-key}")
    private String apiKey;

    private final WebClient client = WebClient.create("https://api.openai.com/v1/responses");

    public JsonNode call(String model, String prompt) {

        ObjectNode body = JsonNodeFactory.instance.objectNode();
        body.put("model", model);
        body.put("input", prompt);

        return client.post()
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}


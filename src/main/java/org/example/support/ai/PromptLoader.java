package org.example.support.ai;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Component
public class PromptLoader {

    public String load(String file, Map<String, String> values) {
        try {
            String prompt = Files.readString(
                    Paths.get("src/main/resources/prompts/" + file)
            );
            for (var entry : values.entrySet()) {
                prompt = prompt.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            return prompt;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


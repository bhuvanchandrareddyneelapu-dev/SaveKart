package com.savekart.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${app.gemini.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateShoppingResponse(String prompt, String databaseContext) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.info("No Gemini API key configured. Using data-driven AI assistant engine.");
            return null;
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

            String systemPrompt = "You are SaveKart AI Shopping Assistant, an expert product price comparison assistant for India. " +
                    "Use the following catalog database context to answer the user request concisely with actionable deal suggestions:\n" +
                    databaseContext + "\n\nUser Question: " + prompt;

            Map<String, Object> textPart = Map.of("text", systemPrompt);
            Map<String, Object> contentObj = Map.of("parts", List.of(textPart));
            Map<String, Object> requestBody = Map.of("contents", List.of(contentObj));

            Map<?, ?> response = restTemplate.postForObject(url, requestBody, Map.class);
            if (response != null && response.containsKey("candidates")) {
                List<?> candidates = (List<?>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
                    Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
                    List<?> parts = (List<?>) content.get("parts");
                    Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);
                    return (String) firstPart.get("text");
                }
            }
        } catch (Exception e) {
            logger.warn("Gemini API call failed: {}. Falling back to default assistant.", e.getMessage());
        }
        return null;
    }
}

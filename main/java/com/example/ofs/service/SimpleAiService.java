package com.example.ofs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class SimpleAiService {


    @Value("${groq.api.key}")
    private String apiKey;


    private final String URL = "https://api.groq.com/openai/v1/chat/completions";


    public String getFinancialAdvice(String userPrompt, double balance, String transactionHistory) {
        RestTemplate restTemplate = new RestTemplate();


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);


        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a professional financial advisor. User balance is ₹" + balance),
                        Map.of("role", "user", "content", userPrompt + "\n\nContext (Recent Transactions): " + transactionHistory)
                ),
                "temperature", 0.7 // Adds a touch of personality to the response
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(URL, entity, Map.class);

            if (response != null && response.containsKey("choices")) {

                List<?> choices = (List<?>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                    return (String) message.get("content");
                }
            }
            return "AI returned an empty response. Please try again.";

        } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
            return "Rate limit reached. Please wait a few seconds before asking again.";
        } catch (Exception e) {

            System.err.println("Groq API Error: " + e.getMessage());
            return "AI Error: I'm currently having trouble analyzing your finances. Details: " + e.getMessage();
        }
    }
}
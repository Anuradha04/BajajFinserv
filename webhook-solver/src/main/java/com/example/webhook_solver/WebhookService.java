package com.example.webhook_solver;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void execute() {
        // Step 1: Send initial POST request
        String initUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = Map.of(
            "name", "John Doe",
            "regNo", "REG12347",
            "email", "john@example.com"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<WebhookResponse> response = restTemplate.exchange(
            initUrl, HttpMethod.POST, entity, WebhookResponse.class
        );

        String webhookUrl = response.getBody().getWebhook();
        String accessToken = response.getBody().getAccessToken();

        System.out.println("Access Token: " + accessToken);
        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Is token null? " + (accessToken == null));
        System.out.println("Is webhook URL null? " + (webhookUrl == null));


        String finalQuery = getSqlQuery("REG12347");

        submitFinalQuery(webhookUrl, accessToken, finalQuery);
    }

    private String getSqlQuery(String regNo) {
        int lastDigit = Character.getNumericValue(regNo.charAt(regNo.length() - 1));
        boolean isOdd = lastDigit % 2 != 0;

        return isOdd
            ? "SELECT * FROM question1_table;"  // Placeholder for Question 1
            : "SELECT * FROM question2_table;"; // Placeholder for Question 2
    }

    private void submitFinalQuery(String webhookUrl, String token, String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, String> body = Map.of("finalQuery", query);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, entity, String.class);
        System.out.println("Submission Response: " + response.getBody());
        System.out.println("Submitting to: " + webhookUrl);
        System.out.println("Using token: Bearer " + token);
        System.out.println("Final query: " + query);

    }
}

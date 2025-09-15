package com.example.smartgridmetering.Client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Component
public class MLClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8000/predict";

    public Double getPrediction(List<Double> features) {
        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("features", features);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Send POST request with type-safe response
        ResponseEntity<Map<String, List<Double>>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Map<String, List<Double>>>() {}
        );

        List<Double> predictions = response.getBody().get("prediction");
        return predictions.get(0); // return the first prediction
    }
}

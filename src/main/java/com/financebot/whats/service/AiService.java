package com.financebot.whats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financebot.whats.dto.FinanceMessageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public FinanceMessageDTO interpretMessage(String message) {

        try {
            String prompt = """
            Extraia os dados da mensagem abaixo.

            Responda SOMENTE em JSON válido, sem texto extra.
            Campos:
            - tipo (gasto ou receita)
            - valor (number)
            - categoria (string)

            Mensagem: "%s"
            """.formatted(message);

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/"
                            + "gemini-1.5-flash:generateContent?key=" + apiKey;

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, request, String.class);

            JsonNode root = mapper.readTree(response.getBody());

            String content = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return mapper.readValue(content, FinanceMessageDTO.class);

        } catch (Exception e) {
            return null;
        }
    }
}

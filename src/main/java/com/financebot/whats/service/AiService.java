package com.financebot.whats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financebot.whats.dto.AiResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Ordem de prioridade dos modelos.
     * Se um atingir o limite (429), o próximo será utilizado.
     */
    private static final List<String> MODELOS = List.of(
            "gemini-2.5-flash-lite",
            "gemini-3.1-flash-lite",
            "gemini-2.5-flash"
    );

    public AiResponseDTO interpretMessage(String message) {

        try {

            String prompt = """
                    Extraia os dados da mensagem abaixo.

                    Responda SOMENTE em JSON válido, sem texto extra.

                    Campos:
                    - tipo (gasto ou receita)
                    - valor (number)
                    - categoria (string)

                    Exemplo:
                    {
                      "tipo":"gasto",
                      "valor":25.90,
                      "categoria":"alimentação"
                    }

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

            for (String model : MODELOS) {

                try {

                    System.out.println("Tentando modelo: " + model);

                    ResponseEntity<String> response =
                            chamarModelo(model, request);

                    JsonNode root = mapper.readTree(response.getBody());

                    String content = root
                            .path("candidates").get(0)
                            .path("content")
                            .path("parts").get(0)
                            .path("text")
                            .asText()
                            .replace("```json", "")
                            .replace("```", "")
                            .trim();

                    System.out.println("Modelo utilizado: " + model);

                    return mapper.readValue(content, AiResponseDTO.class);

                } catch (HttpStatusCodeException e) {

                    if (e.getStatusCode().value() == 429) {

                        System.out.println(
                                "Limite atingido para " + model +
                                        ". Tentando próximo modelo..."
                        );

                        continue;
                    }

                    throw e;
                }
            }

            System.out.println("Todos os modelos atingiram o limite.");
            return null;

        } catch (Exception e) {

            System.out.println("Erro no AiService:");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Faz a chamada para qualquer modelo do Gemini.
     */
    private ResponseEntity<String> chamarModelo(
            String model,
            HttpEntity<Map<String, Object>> request
    ) {

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + model
                        + ":generateContent?key="
                        + apiKey;

        return restTemplate.postForEntity(
                url,
                request,
                String.class
        );
    }
}
package com.financebot.whats.controller;

import com.financebot.whats.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        String credential = body.get("credential");

        if (credential == null || credential.isBlank()) {
            return ResponseEntity.badRequest().body("Token ausente");
        }

        try {
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + credential;
            Map response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.get("email") == null) {
                return ResponseEntity.status(401).body("Token inválido");
            }

            String email = response.get("email").toString();
            String nome = response.getOrDefault("name", email).toString();
            String foto = response.getOrDefault("picture", "").toString();

            String jwt = jwtService.generateToken(email);

            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "email", email,
                    "nome", nome,
                    "foto", foto
            ));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido");
        }
    }
}
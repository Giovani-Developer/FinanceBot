package com.financebot.whats.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        System.out.println("================================");
        System.out.println("URI: " + request.getRequestURI());

        String header = request.getHeader("Authorization");
        System.out.println("Authorization: " + header);

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            System.out.println("TOKEN:");
            System.out.println(token);

            try {

                System.out.println("TOKEN VÁLIDO? " + jwtService.isValid(token));

                String email = jwtService.extractEmail(token);

                System.out.println("EMAIL = " + email);

                var auth = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of()
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("AUTENTICADO!");

            } catch (Exception e) {

                System.out.println("ERRO JWT");
                e.printStackTrace();

            }
        }

        chain.doFilter(request, response);
    }
}
package com.financebot.whats.dto;


public record FinanceMessageDTO(
        String tipo,
        Double valor,
        String categoria
) {}

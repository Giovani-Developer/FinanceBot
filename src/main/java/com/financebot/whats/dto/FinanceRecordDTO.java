package com.financebot.whats.dto;

import java.time.LocalDateTime;

public class FinanceRecordDTO {
    private Long id;
    private String tipo;
    private Double valor;
    private String categoria;
    private LocalDateTime createdAt;

    public FinanceRecordDTO(Long id, String tipo, Double valor, String categoria, LocalDateTime createdAt) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.categoria = categoria;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

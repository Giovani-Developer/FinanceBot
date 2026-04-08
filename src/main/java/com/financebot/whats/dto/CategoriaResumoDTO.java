package com.financebot.whats.dto;

public class CategoriaResumoDTO {
    private String categoria;
    private Double totalGasto;
    private Double totalReceita;
    private Long quantidade;

    public CategoriaResumoDTO(String categoria, Double totalGasto, Double totalReceita, Long quantidade) {
        this.categoria = categoria;
        this.totalGasto = totalGasto;
        this.totalReceita = totalReceita;
        this.quantidade = quantidade;
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getTotalGasto() { return totalGasto; }
    public void setTotalGasto(Double totalGasto) { this.totalGasto = totalGasto; }

    public Double getTotalReceita() { return totalReceita; }
    public void setTotalReceita(Double totalReceita) { this.totalReceita = totalReceita; }

    public Long getQuantidade() { return quantidade; }
    public void setQuantidade(Long quantidade) { this.quantidade = quantidade; }
}

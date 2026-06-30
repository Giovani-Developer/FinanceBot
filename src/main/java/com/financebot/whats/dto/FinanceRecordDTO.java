package com.financebot.whats.dto;

import java.time.LocalDateTime;

public class FinanceRecordDTO {
    private Long id;
    private String tipo;
    private Double valor;
    private String categoria;
    private LocalDateTime createdAt;
    private Boolean parcelado;
    private Integer totalParcelas;
    private Integer parcelaAtual;
    private Boolean pago;


    public FinanceRecordDTO(Long id, String tipo, Double valor, String categoria, LocalDateTime createdAt, Boolean parcelado, Integer totalParcelas, Integer parcelaAtual, Boolean pago) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.categoria = categoria;
        this.createdAt = createdAt;
        this.parcelado = parcelado;
        this.totalParcelas = totalParcelas;
        this.parcelaAtual = parcelaAtual;
        this.pago = pago;
    }

    public Long getId() { return id; }

    public String getTipo() { return tipo; }

    public Double getValor() { return valor; }

    public String getCategoria() { return categoria; }

    public LocalDateTime getCreatedAt() { return createdAt; }


    public Boolean getParcelado() { return parcelado; }

    public Integer getTotalParcelas() { return totalParcelas; }

    public Integer getParcelaAtual() { return parcelaAtual; }

    public Boolean getPago() { return pago; }

    public void setId(Long id) { this.id = id; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public void setValor(Double valor) { this.valor = valor; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setParcelado(Boolean parcelado) { this.parcelado = parcelado; }

    public void setTotalParcelas(Integer totalParcelas) { this.totalParcelas = totalParcelas; }

    public void setParcelaAtual(Integer parcelaAtual) { this.parcelaAtual = parcelaAtual; }

    public void setPago(Boolean pago) { this.pago = pago; }

    // Calculo do valor restante das parcelas
    public Double getValorRestante() {
        if (!Boolean.TRUE.equals(parcelado) || totalParcelas == null || parcelaAtual == null) return null;
        return valor * (totalParcelas - parcelaAtual);
    }
}

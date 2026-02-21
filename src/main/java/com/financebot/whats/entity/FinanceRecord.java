package com.financebot.whats.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "finance_records")
@Getter
@Setter
public class FinanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userPhone;
    private String tipo;
    private Double valor;
    private String categoria;

    private LocalDateTime createdAt = LocalDateTime.now();
}

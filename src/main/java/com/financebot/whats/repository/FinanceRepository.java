package com.financebot.whats.repository;

import com.financebot.whats.entity.FinanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinanceRepository extends JpaRepository<FinanceRecord, Long> {

    List<FinanceRecord> findByUserPhone(String userPhone);

    @Query("SELECT SUM(r.valor) FROM FinanceRecord r WHERE r.userPhone = :user AND r.tipo = :tipo")
    Double sumByUserAndTipo(@Param("user") String user, @Param("tipo") String tipo);

    // Historico com filtros opcionais
    @Query("SELECT r FROM FinanceRecord r " +
            "WHERE r.userPhone = :user " +
            "AND (:dataInicio IS NULL OR r.createdAt >= :dataInicio) " +
            "AND (:dataFim IS NULL OR r.createdAt <= :dataFim) " +
            "AND (:categoria IS NULL OR r.categoria = :categoria) " +
            "AND (:tipo IS NULL OR r.tipo = :tipo) " +
            "ORDER BY r.createdAt DESC")
    Page<FinanceRecord> findHistorico(
            @Param("user") String user,
            @Param("dataInicio") java.time.LocalDateTime dataInicio,
            @Param("dataFim") java.time.LocalDateTime dataFim,
            @Param("categoria") String categoria,
            @Param("tipo") String tipo,
            Pageable pageable);

    // Lista todos para agregacao no service
    @Query("SELECT r FROM FinanceRecord r WHERE r.userPhone = :user ORDER BY r.createdAt DESC")
    List<FinanceRecord> findByUserPhoneOrdered(@Param("user") String user);

    // Deletar com validacao de dono
    Optional<FinanceRecord> findByIdAndUserPhone(Long id, String userPhone);
}

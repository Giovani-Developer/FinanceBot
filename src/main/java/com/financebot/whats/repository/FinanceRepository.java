package com.financebot.whats.repository;

import com.financebot.whats.entity.FinanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinanceRepository extends JpaRepository<FinanceRecord, Long> {

    // Busca simples por usuário
    List<FinanceRecord> findByUserPhone(String userPhone);

    // Soma por tipo (gasto ou receita)
    @Query("SELECT SUM(r.valor) FROM FinanceRecord r WHERE r.userPhone = :user AND r.tipo = :tipo")
    Double sumByUserAndTipo(@Param("user") String user, @Param("tipo") String tipo);

    // Busca todos ordenados por data (usado no histórico e resumo)
    @Query("SELECT r FROM FinanceRecord r WHERE r.userPhone = :user ORDER BY r.createdAt DESC")
    List<FinanceRecord> findAllByUser(@Param("user") String user);

    // Alias para compatibilidade com getResumo e getCategoriaResumo
    @Query("SELECT r FROM FinanceRecord r WHERE r.userPhone = :user ORDER BY r.createdAt DESC")
    List<FinanceRecord> findByUserPhoneOrdered(@Param("user") String user);

    // Busca por id e usuário (validação de dono antes de deletar)
    Optional<FinanceRecord> findByIdAndUserPhone(Long id, String userPhone);
}
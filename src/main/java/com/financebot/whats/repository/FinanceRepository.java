package com.financebot.whats.repository;

import com.financebot.whats.entity.FinanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceRepository extends JpaRepository<FinanceRecord, Long> {
}

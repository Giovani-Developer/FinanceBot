package com.financebot.whats.service;


import com.financebot.whats.entity.FinanceRecord;
import com.financebot.whats.repository.FinanceRepository;
import org.springframework.stereotype.Service;

@Service

public class FinanceService {

    private final FinanceRepository repository;

    public FinanceService(FinanceRepository repository) {
        this.repository = repository;
    }

    public String processMessage(String message, String user) {

        FinanceRecord record = new FinanceRecord();
        record.setUserPhone(user);
        record.setTipo("gasto");
        record.setValor(30.0);
        record.setCategoria("message");


        repository.save(record);

        return "Anotado ✅ gasto de R$ 30.00 em mercado";
    }

}

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

        String[] parts = message.split(" ");

        String tipo = parts[0]; // gastei
        double valor = Double.parseDouble(parts[1]); // valor gasto
        String categoria = parts[2]; // onde ele gastou


        FinanceRecord record = new FinanceRecord();
        record.setUserPhone(user);
        record.setTipo(tipo);
        record.setValor(valor);
        record.setCategoria(categoria);


        repository.save(record);

        return "Anotado!!" + tipo + " de R$ " + valor + " em " + categoria;
    }

}

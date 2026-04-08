package com.financebot.whats.service;


import com.financebot.whats.dto.AiResponseDTO;
import com.financebot.whats.entity.FinanceRecord;
import com.financebot.whats.dto.FinanceRecordDTO;
import com.financebot.whats.repository.FinanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinanceService {

    private final FinanceRepository repository;
    private final AiService aiService;

    public FinanceService(FinanceRepository repository, AiService aiService) {
        this.repository = repository;
        this.aiService = aiService;
    }

    public String processMessage(String message, String user) {

        AiResponseDTO ai = aiService.interpretMessage(message);

        if (ai == null) {
            return "Não consegui entender sua mensagem. Tente algo como: 'gastei 30 mercado'";
        }

        FinanceRecord record = new FinanceRecord();
        record.setUserPhone(user);
        record.setTipo(ai.getTipo());
        record.setValor(ai.getValor());
        record.setCategoria(ai.getCategoria());

        repository.save(record);

        return "Anotado! " + ai.getTipo() + " de R$ " + ai.getValor() + " em " + ai.getCategoria();
    }

    public String getResumo(String user) {
        Double gastos = repository.sumByUserAndTipo(user, "gasto");
        Double receitas = repository.sumByUserAndTipo(user, "receita");

        gastos = gastos != null ? gastos : 0;
        receitas = receitas != null ? receitas : 0;

        List<FinanceRecord> all = repository.findByUserPhoneOrdered(user);
        Double maiorGasto = all.stream()
                .filter(r -> "gasto".equals(r.getTipo()))
                .mapToDouble(FinanceRecord::getValor)
                .max()
                .orElse(0);

        LocalDate now = LocalDate.now();
        Double gastoMesAtual = all.stream()
                .filter(r -> "gasto".equals(r.getTipo()) && r.getCreatedAt().getMonth().getValue() == now.getMonthValue() && r.getCreatedAt().getYear() == now.getYear())
                .mapToDouble(FinanceRecord::getValor)
                .sum();

        LocalDate mesAnterior = now.minusMonths(1);
        Double gastoMesAnterior = all.stream()
                .filter(r -> "gasto".equals(r.getTipo()) && r.getCreatedAt().getMonth().getValue() == mesAnterior.getMonthValue() && r.getCreatedAt().getYear() == mesAnterior.getYear())
                .mapToDouble(FinanceRecord::getValor)
                .sum();

        return "Resumo de " + user + ":\n" +
                "Receitas: R$ " + String.format("%.2f", receitas) + "\n" +
                "Gastos: R$ " + String.format("%.2f", gastos) + "\n" +
                "Saldo: R$ " + String.format("%.2f", receitas - gastos) + "\n" +
                "Maior gasto: R$ " + String.format("%.2f", maiorGasto) + "\n" +
                "Gasto este mês: R$ " + String.format("%.2f", gastoMesAtual) + "\n" +
                "Gasto mês anterior: R$ " + String.format("%.2f", gastoMesAnterior) + "\n" +
                "Total de transações: " + all.size();
    }

    // Historico paginado com filtros
    public Page<FinanceRecordDTO> getHistorico(
            String user,
            LocalDate dataInicio,
            LocalDate dataFim,
            String categoria,
            String tipo,
            Pageable pageable) {

        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.plusDays(1).atStartOfDay() : null;

        Page<FinanceRecord> page = repository.findHistorico(user, inicio, fim, categoria, tipo, pageable);

        return page.map(r -> new FinanceRecordDTO(
                r.getId(), r.getTipo(), r.getValor(), r.getCategoria(), r.getCreatedAt()
        ));
    }

    // Resumo por categoria (agregacao feita em Java, nao JPQL)
    public List<com.financebot.whats.dto.CategoriaResumoDTO> getCategoriaResumo(
            String user,
            LocalDate dataInicio,
            LocalDate dataFim) {

        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.plusDays(1).atStartOfDay() : null;

        List<FinanceRecord> records = repository.findByUserPhoneOrdered(user);

        // Filtra por data no service
        List<FinanceRecord> filtered = records.stream()
                .filter(r -> inicio == null || !r.getCreatedAt().isBefore(inicio))
                .filter(r -> fim == null || !r.getCreatedAt().isAfter(fim))
                .toList();

        // Agrupa por categoria com LinkedHashMap para manter ordem
        Map<String, Double[]> categoriasMap = new LinkedHashMap<>();
        for (FinanceRecord r : filtered) {
            String cat = r.getCategoria() != null ? r.getCategoria() : "Sem categoria";
            categoriasMap.putIfAbsent(cat, new Double[]{0.0, 0.0, 0.0});
            if ("gasto".equals(r.getTipo())) {
                categoriasMap.get(cat)[0] += r.getValor() != null ? r.getValor() : 0;
            } else if ("receita".equals(r.getTipo())) {
                categoriasMap.get(cat)[1] += r.getValor() != null ? r.getValor() : 0;
            }
            categoriasMap.get(cat)[2]++;
        }

        return categoriasMap.entrySet().stream()
                .map(e -> new com.financebot.whats.dto.CategoriaResumoDTO(
                        e.getKey(), e.getValue()[0], e.getValue()[1], e.getValue()[2].longValue()
                ))
                .toList();
    }

    // Deletar transacao
    public boolean deletarTransacao(Long id, String user) {
        return repository.findByIdAndUserPhone(id, user).map(record -> {
            repository.delete(record);
            return true;
        }).orElse(false);
    }

}

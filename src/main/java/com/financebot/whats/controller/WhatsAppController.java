package com.financebot.whats.controller;


import com.financebot.whats.dto.CategoriaResumoDTO;
import com.financebot.whats.dto.FinanceMessageDTO;
import com.financebot.whats.dto.FinanceRecordDTO;
import com.financebot.whats.service.FinanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/finance")
@CrossOrigin(origins = "*")
public class WhatsAppController {

    private final FinanceService financeService;

    public WhatsAppController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @PostMapping("/message")
    public String receiveMessage(
            @RequestBody FinanceMessageDTO dto) {
        return financeService.processMessage(dto.getMessage(), dto.getUser());
    }

    @GetMapping("/resumo/{user}")
    public String getResumo(@PathVariable String user) {
        return financeService.getResumo(user);
    }

    @GetMapping("/historico/{user}")
    public Page<FinanceRecordDTO> getHistorico(
            @PathVariable String user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return financeService.getHistorico(user, dataInicio, dataFim, categoria, tipo, pageable);
    }

    @GetMapping("/categoria-resumo/{user}")
    public List<CategoriaResumoDTO> getCategoriaResumo(
            @PathVariable String user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        return financeService.getCategoriaResumo(user, dataInicio, dataFim);
    }

    @DeleteMapping("/transacao/{id}")
    public ResponseEntity<String> deletarTransacao(
            @PathVariable Long id,
            @RequestParam String user) {

        boolean deleted = financeService.deletarTransacao(id, user);
        if (deleted) {
            return ResponseEntity.ok("Transação deletada com sucesso");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Transação não encontrada ou não pertence ao usuário");
    }
}

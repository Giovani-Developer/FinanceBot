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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/finance")
public class WhatsAppController {

    private final FinanceService financeService;

    public WhatsAppController(FinanceService financeService) {
        this.financeService = financeService;
    }

    private String getEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
        }
        return (String) auth.getPrincipal();
    }

    @PostMapping("/message")
    public String receiveMessage(@RequestBody FinanceMessageDTO dto) {
        dto.setUser(getEmail());
        return financeService.processMessage(dto);
    }

    @GetMapping("/resumo")
    public String getResumo() {
        return financeService.getResumo(getEmail());
    }

    @GetMapping("/historico")
    public Page<FinanceRecordDTO> getHistorico(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return financeService.getHistorico(getEmail(), dataInicio, dataFim, categoria, tipo, pageable);
    }

    @GetMapping("/categoria-resumo")
    public List<CategoriaResumoDTO> getCategoriaResumo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return financeService.getCategoriaResumo(getEmail(), dataInicio, dataFim);
    }

    @DeleteMapping("/transacao/{id}")
    public ResponseEntity<String> deletarTransacao(@PathVariable Long id) {
        boolean deleted = financeService.deletarTransacao(id, getEmail());
        return deleted ? ResponseEntity.ok("Deletado")
                : ResponseEntity.status(404).body("Não encontrado");
    }

    @PatchMapping("/transacao/{id}/pagar")
    public ResponseEntity<String> marcarComoPago(@PathVariable Long id) {
        boolean ok = financeService.marcarComoPago(id, getEmail());
        return ok ? ResponseEntity.ok("Marcado como pago!")
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/transacao/{id}/proxima-parcela")
    public ResponseEntity<String> pagarProximaParcela(@PathVariable Long id) {
        String resultado = financeService.pagarProximaParcela(id, getEmail());
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/parcelas-ativas")
    public List<FinanceRecordDTO> getParcelasAtivas() {
        return financeService.getParcelasAtivas(getEmail());
    }
}
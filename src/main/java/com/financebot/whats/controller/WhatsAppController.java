package com.financebot.whats.controller;


import com.financebot.whats.dto.FinanceMessageDTO;
import com.financebot.whats.service.FinanceService;
import org.springframework.web.bind.annotation.*;

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


}

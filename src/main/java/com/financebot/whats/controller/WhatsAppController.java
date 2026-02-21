package com.financebot.whats.controller;


import com.financebot.whats.service.FinanceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")
public class WhatsAppController {

    private final FinanceService financeService;

    public WhatsAppController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @PostMapping("/message")
    public String receiveMessage(
            @RequestParam String message,
            @RequestParam String user
    ) {
        return financeService.processMessage(message, user);
    }
}

package com.ai.function_calling.controller;

import com.ai.function_calling.service.CurrencyChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CurrencyChatController {
    private final CurrencyChatService currencyChatService;
    @GetMapping("/api/v1/currency/chat")
    public String chat(@RequestParam String message) {
        return currencyChatService.askAi(message);
    }
    @PostMapping("/api/v1/currency/chat")
    public String chatPost(@RequestBody String message) {
        return currencyChatService.askAi(message);
    }
}

package com.ai.ss3_bai1.controller;

import com.ai.ss3_bai1.service.FallbackChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")

public class ChatController {

    private final FallbackChatService fallbackChatService;

    public ChatController(FallbackChatService fallbackChatService) {
        this.fallbackChatService = fallbackChatService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tham số 'message' không được để trống"));
        }

        String aiResponse = fallbackChatService.callWithFallback(message);
        return ResponseEntity.ok(Map.of("response", aiResponse));
    }
}

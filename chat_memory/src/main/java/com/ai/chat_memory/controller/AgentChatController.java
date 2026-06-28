package com.ai.chat_memory.controller;

import com.ai.chat_memory.service.AgentChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/agent-chat")
public class AgentChatController {
    private final AgentChatService agentChatService;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request){
        String chatId = request.get("chatId");
        String message = request.get("message");
        if (chatId == null || chatId.isBlank() || message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thiếu tham số 'chatId' hoặc 'message'"));
        }

        String response = agentChatService.chat(chatId, message);
        return ResponseEntity.ok(Map.of("chatId", chatId, "response", response));
    }


}

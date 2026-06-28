package com.ai.ss3_bai1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FallbackChatService {

    private final ChatModel ollamaChatModel;

    public FallbackChatService(@Qualifier("ollamaChatModel") ChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
    }

    public String callWithFallback(String userPromptText) {
        Prompt prompt = new Prompt(userPromptText);

        try {
            log.info("Đang thử gọi LLM qua Ollama qwen2.5:7b (Local Fallback)");
            ChatResponse response = ollamaChatModel.call(prompt);

            if (response != null && response.getResult() != null) {
                log.info("Gọi thành công qua Ollama qwen2.5:7b (Local Fallback)");
                return response.getResult().getOutput().getText();
            }
            throw new RuntimeException("Phản hồi từ Ollama trống");
        } catch (Exception e) {
            log.error("❌ Lỗi khi gọi Ollama qwen2.5:7b: {}", e.getMessage());
            throw new RuntimeException("Hệ thống AI Ollama tạm thời không khả dụng. Chi tiết: " + e.getMessage());
        }
    }
}

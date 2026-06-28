package com.ai.chat_memory.config;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {
    @Bean
    public ChatMemoryRepository chatMemoryRepository() {
        // Cấu hình bộ nhớ trò chuyện (chat memory) cho Agent
        return new InMemoryChatMemoryRepository();
    }
}

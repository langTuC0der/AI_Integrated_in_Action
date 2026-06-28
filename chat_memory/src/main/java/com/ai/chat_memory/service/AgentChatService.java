package com.ai.chat_memory.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AgentChatService {
    private final ChatClient chatClient;
    private final String activeProvider;

    public AgentChatService(
            @Qualifier("ollamaChatModel") ObjectProvider<ChatModel> ollamaChatModelProvider,
            @Qualifier("openAiChatModel") ObjectProvider<ChatModel> openAiChatModelProvider,
            @Value("${app.chat-provider:ollama}") String provider,
            ChatMemory chatMemory) {
        
        // Lựa chọn Model hoạt động dựa trên cấu hình trong application.properties
        ChatModel activeModel = null;
        String providerName = "";

        if ("deepseek".equalsIgnoreCase(provider)) {
            activeModel = openAiChatModelProvider.getIfAvailable();
            if (activeModel == null) {
                throw new IllegalStateException("❌ DeepSeek (qua OpenAI ChatModel) không khả dụng. Vui lòng kiểm tra cấu hình trong application.properties.");
            }
            providerName = "DeepSeek API (OpenAI Compatible)";
        } else if ("openai".equalsIgnoreCase(provider)) {
            activeModel = openAiChatModelProvider.getIfAvailable();
            if (activeModel == null) {
                throw new IllegalStateException("❌ OpenAI ChatModel không khả dụng. Vui lòng kiểm tra cấu hình API Key.");
            }
            providerName = "OpenAI GPT";
        } else if ("gemini-studio".equalsIgnoreCase(provider)) {
            activeModel = openAiChatModelProvider.getIfAvailable();
            if (activeModel == null) {
                throw new IllegalStateException("❌ Google AI Studio ChatModel không khả dụng. Vui lòng kiểm tra cấu hình API Key.");
            }
            providerName = "Google AI Studio Gemini (OpenAI Compatible)";
        } else {
            activeModel = ollamaChatModelProvider.getIfAvailable();
            if (activeModel == null) {
                throw new IllegalStateException("❌ Ollama ChatModel không khả dụng. Vui lòng kiểm tra xem Ollama đã được khởi động chưa.");
            }
            providerName = "Ollama (" + activeModel.getClass().getSimpleName() + ")";
        }

        this.activeProvider = providerName;
        this.chatClient = ChatClient.builder(activeModel)
                .defaultSystem(
                        "Bạn là trợ lý ảo lịch sự của khách sạn Rikkei Luxury Hotel. Hãy giúp khách hàng đặt phòng. CHÚ Ý: Luôn luôn trả lời khách hàng bằng tiếng Việt")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

        log.info("🤖 AgentChatService đã khởi tạo thành công với LLM Provider hoạt động: [{}]", activeProvider);
    }

    public String chat(String chatId, String message) {
        log.info("🚀 [AgentChatService] Gửi yêu cầu tới Model: [{}] | ChatID: [{}] | Message: \"{}\"", activeProvider, chatId, message);
        return this.chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
    }
}

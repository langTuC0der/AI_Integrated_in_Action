package com.ai.function_calling.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CurrencyChatService {
    private final ChatClient chatClient;
    private final String activeProvider;

    public CurrencyChatService(
            @Qualifier("ollamaChatModel") ObjectProvider<ChatModel> ollamaChatModelProvider,
            @Qualifier("openAiChatModel") ObjectProvider<ChatModel> openAiChatModelProvider,
            @Value("${app.chat-provider:ollama}") String provider) {
        
        // Lựa chọn Model hoạt động dựa trên cấu hình trong application.properties
        ChatModel activeModel = null;
        String providerName = "";
        
        if ("gemini".equalsIgnoreCase(provider)) {
            activeModel = openAiChatModelProvider.getIfAvailable();
            if (activeModel == null) {
                throw new IllegalStateException("❌ Google AI Studio ChatModel không khả dụng. Vui lòng kiểm tra cấu hình API Key.");
            }
            providerName = "Google AI Studio Gemini";
        } else {
            activeModel = ollamaChatModelProvider.getIfAvailable();
            if (activeModel == null) {
                throw new IllegalStateException("❌ Ollama ChatModel không khả dụng. Vui lòng kiểm tra xem Ollama đã được khởi động chưa.");
            }
            providerName = "Ollama (" + activeModel.getClass().getSimpleName() + ")";
        }

        this.activeProvider = providerName;
        this.chatClient = ChatClient.builder(activeModel)
                .defaultSystem("Bạn là trợ lý hỗ trợ chuyển đổi tiền tệ và tính toán tài chính.")
                .build();

        log.info("🤖 CurrencyChatService đã khởi tạo thành công với LLM Provider hoạt động: [{}]", activeProvider);
    }

    public String askAi(String promptText) {
        log.info("🚀 [CurrencyChatService] Gửi yêu cầu tới Model: [{}] với prompt: \"{}\"", activeProvider, promptText);
        return this.chatClient.prompt()
                .user(promptText)
                // Kích hoạt function bằng cách truyền tên Bean của Function vào đây
                .toolNames("exchangeCurrency")
                .call()
                .content();
    }
}

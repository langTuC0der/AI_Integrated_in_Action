package com.microservice.test_ai.service;

import com.microservice.test_ai.config.TeacherStylePrompt;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to interact with the LLM model in a loosely-coupled way.
 */
@Service
public class ChatService {

    private static final int MAX_MEMORY_MESSAGES = 12;

    private final ChatModel chatModel;
    private final Map<String, List<ChatTurn>> conversations = new ConcurrentHashMap<>();

    // ChatModel is auto-injected by Spring based on the configured starter
    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Basic chat call with a single user message.
     */
    public String simpleChat(String userMessage) {
        List<Message> messages = List.of(
            new SystemMessage(TeacherStylePrompt.VIETNAMESE_AI_LECTURER),
            new UserMessage(userMessage)
        );
        Prompt prompt = new Prompt(messages);
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

    /**
     * Chat call with System Prompt (role setup) and User Message.
     */
    public String chatWithRole(String systemPrompt, String userMessage) {
        List<Message> messages = List.of(
            new SystemMessage(systemPrompt),
            new UserMessage(userMessage)
        );
        Prompt prompt = new Prompt(messages);
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

    /**
     * Chat call with usage tracking metadata (tokens information).
     */
    public Map<String, Object> chatWithUsageTracking(String userMessage) {
        List<Message> messages = List.of(
            new SystemMessage(TeacherStylePrompt.VIETNAMESE_AI_LECTURER),
            new UserMessage(userMessage)
        );
        ChatResponse response = chatModel.call(new Prompt(messages));
        var usage = response.getMetadata().getUsage();

        return Map.of(
            "content",       response.getResult().getOutput().getText(),
            "inputTokens",   usage.getPromptTokens() != null ? usage.getPromptTokens() : 0,
            "outputTokens",  usage.getCompletionTokens() != null ? usage.getCompletionTokens() : 0,
            "totalTokens",   usage.getTotalTokens() != null ? usage.getTotalTokens() : 0
        );
    }

    /**
     * Chat with short-term conversation memory. The browser sends the same
     * conversationId for follow-up questions, so the model can see prior turns.
     */
    public Map<String, Object> chatWithMemory(String conversationId, String userMessage) {
        String safeConversationId = conversationId == null || conversationId.isBlank()
            ? "default"
            : conversationId;

        List<ChatTurn> history = conversations.computeIfAbsent(
            safeConversationId,
            ignored -> new ArrayList<>()
        );

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(TeacherStylePrompt.VIETNAMESE_AI_LECTURER + """

            Bạn đang nói chuyện trong một cuộc hội thoại liên tục.
            Hãy dùng các tin nhắn trước đó để hiểu các câu hỏi nối tiếp như "nó", "cái đó", "ví dụ tiếp", "ý trên".
            Nếu người học hỏi lại, hãy liên hệ đến nội dung đã trao đổi trước đó.
            """));

        synchronized (history) {
            for (ChatTurn turn : history) {
                if ("user".equals(turn.role())) {
                    messages.add(new UserMessage(turn.content()));
                } else {
                    messages.add(new AssistantMessage(turn.content()));
                }
            }
            messages.add(new UserMessage(userMessage));
        }

        ChatResponse response = chatModel.call(new Prompt(messages));
        String answer = response.getResult().getOutput().getText();
        var usage = response.getMetadata().getUsage();

        synchronized (history) {
            history.add(new ChatTurn("user", userMessage));
            history.add(new ChatTurn("assistant", answer));
            trimHistory(history);
        }

        return Map.of(
            "conversationId", safeConversationId,
            "content", answer,
            "memoryMessages", history.size(),
            "inputTokens", usage.getPromptTokens() != null ? usage.getPromptTokens() : 0,
            "outputTokens", usage.getCompletionTokens() != null ? usage.getCompletionTokens() : 0,
            "totalTokens", usage.getTotalTokens() != null ? usage.getTotalTokens() : 0
        );
    }

    public void clearConversation(String conversationId) {
        if (conversationId != null && !conversationId.isBlank()) {
            conversations.remove(conversationId);
        }
    }

    private void trimHistory(List<ChatTurn> history) {
        while (history.size() > MAX_MEMORY_MESSAGES) {
            history.remove(0);
        }
    }

    private record ChatTurn(String role, String content) {}
}

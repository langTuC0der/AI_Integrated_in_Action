package com.microservice.test_ai.controller;

import com.microservice.test_ai.config.TeacherStylePrompt;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.messages.Message;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

/**
 * Controller for streaming (Server-Sent Events) chat requests with LLMs.
 */
@RestController
@RequestMapping("/api/chat/stream")
@CrossOrigin(origins = "*")
public class StreamingChatController {

    private final ChatModel chatModel;

    public StreamingChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * GET /api/chat/stream?message=Hello
     * Content-Type: text/event-stream
     * Streams the text word-by-word like ChatGPT.
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String message) {
        ChatOptions options = ChatOptions.builder()
            .temperature(0.7)
            .build();

        var messages = new ArrayList<Message>();
        messages.add(new org.springframework.ai.chat.messages.SystemMessage(TeacherStylePrompt.VIETNAMESE_AI_LECTURER));
        messages.add(new org.springframework.ai.chat.messages.UserMessage(message));

        Prompt prompt = new Prompt(messages, options);

        return chatModel.stream(prompt)
            .map(response -> {
                if (response.getResult() != null && response.getResult().getOutput() != null) {
                    String content = response.getResult().getOutput().getText();
                    return content != null ? content : "";
                }
                return "";
            })
            .filter(content -> !content.isEmpty());
    }

    /**
     * POST /api/chat/stream
     * Request body: JSON specifying prompt and parameters.
     * Content-Type: text/event-stream
     */
    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChatWithOptions(@RequestBody StreamRequest request) {
        ChatOptions options = ChatOptions.builder()
            .temperature(request.temperature() > 0 ? (double) request.temperature() : 0.7)
            .build();

        var messages = new ArrayList<Message>();
        if (request.systemPrompt() != null && !request.systemPrompt().isBlank()) {
            messages.add(new org.springframework.ai.chat.messages.SystemMessage(request.systemPrompt()));
        } else {
            messages.add(new org.springframework.ai.chat.messages.SystemMessage(TeacherStylePrompt.VIETNAMESE_AI_LECTURER));
        }
        messages.add(new org.springframework.ai.chat.messages.UserMessage(request.message()));

        Prompt prompt = new Prompt(messages, options);

        return chatModel.stream(prompt)
            .map(response -> {
                if (response.getResult() != null && response.getResult().getOutput() != null) {
                    String content = response.getResult().getOutput().getText();
                    return content != null ? content : "";
                }
                return "";
            })
            .filter(content -> !content.isEmpty());
    }

    // DTO for stream config
    public record StreamRequest(
        String message,
        String systemPrompt,
        float temperature,
        int maxTokens
    ) {}
}

package com.microservice.test_ai.service;

import com.microservice.test_ai.model.BookingRequest;
import com.microservice.test_ai.config.BookingPrompts;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to execute the ETL process of extracting hotel booking info from chat message.
 */
@Service
public class BookingExtractionService {

    private final ChatModel chatModel;

    public BookingExtractionService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Extracts booking details from natural text.
     * Uses Temperature = 0.0 for consistent data extraction.
     */
    public BookingRequest extractBookingInfo(String customerMessage) {
        // 1. Create target BeanOutputConverter
        BeanOutputConverter<BookingRequest> converter = new BeanOutputConverter<>(BookingRequest.class);

        // 2. Build user prompt instruction
        String userPromptText = """
            Tin nhắn của khách hàng:
            "%s"
            
            Hãy trích xuất thông tin đặt phòng và trả về theo đúng định dạng sau:
            %s
            """.formatted(customerMessage, converter.getFormat());

        // 3. Prepare messages typed as List<Message> (to prevent Java generic list mismatch)
        List<Message> messages = List.of(
            new SystemMessage(BookingPrompts.BOOKING_EXTRACTION_SYSTEM),
            new UserMessage(userPromptText)
        );

        // 4. Configure ChatOptions portably (Loosely coupled) with Temperature 0.0
        ChatOptions options = ChatOptions.builder()
            .temperature(0.0) // 0.0 means deterministic and consistent output
            .build();

        Prompt prompt = new Prompt(messages, options);

        // 5. Call LLM and parse response using getText()
        String rawResponse = chatModel.call(prompt).getResult().getOutput().getText();
        return converter.convert(rawResponse);
    }
}

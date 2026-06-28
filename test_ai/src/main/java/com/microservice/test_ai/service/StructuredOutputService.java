package com.microservice.test_ai.service;

import com.microservice.test_ai.model.PersonInfo;
import com.microservice.test_ai.model.ProductExtraction;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service that demonstrates how to map LLM's raw text response to Java Objects
 * dynamically using BeanOutputConverter and JSON Schema.
 */
@Service
public class StructuredOutputService {

    private final ChatModel chatModel;

    public StructuredOutputService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Extracts PersonInfo from natural text.
     */
    public PersonInfo extractPersonInfo(String rawText) {
        // 1. Create a converter specifying target class. This generates the JSON Schema.
        BeanOutputConverter<PersonInfo> converter = new BeanOutputConverter<>(PersonInfo.class);
        String formatInstruction = converter.getFormat();

        // 2. Build the prompt including the instructions on format
        String promptText = """
            Hãy trích xuất thông tin từ đoạn văn bản sau và trả về theo đúng cấu trúc yêu cầu.
            Nếu thông tin nào không có trong văn bản, hãy trả về null.
            
            Văn bản: {text}
            
            {format}
            """;

        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(
            Map.of("text", rawText, "format", formatInstruction)
        );

        // 3. Call the LLM
        String rawResponse = chatModel.call(prompt).getResult().getOutput().getText();

        // 4. Convert the raw JSON text response to a type-safe Java Record
        return converter.convert(rawResponse);
    }

    /**
     * Extracts a list of products from orders text.
     */
    public List<ProductExtraction> extractProducts(String orderText) {
        // Using ParameterizedTypeReference for generic List mapping
        var converter = new BeanOutputConverter<>(
            new org.springframework.core.ParameterizedTypeReference<List<ProductExtraction>>() {}
        );

        String prompt = """
            Trích xuất danh sách sản phẩm từ đơn hàng sau.
            Trả về danh sách JSON theo cấu trúc yêu cầu.
            
            Đơn hàng: %s
            
            %s
            """.formatted(orderText, converter.getFormat());

        String rawResponse = chatModel.call(new Prompt(prompt)).getResult().getOutput().getText();
        return converter.convert(rawResponse);
    }
}

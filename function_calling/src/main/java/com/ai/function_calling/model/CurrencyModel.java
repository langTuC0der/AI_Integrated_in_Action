package com.ai.function_calling.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.context.annotation.Configuration;


public class CurrencyModel {
    // JsonPropertyDescription rất quan trọng để LLM hiểu mục đích của các biến truyền vào
    public record Request(
            @JsonPropertyDescription("Số tiền cần chuyển đổi, ví dụ: 100")
            double amount,
            @JsonPropertyDescription("Mã tiền tệ gốc (3 ký tự viết hoa), ví dụ: USD, EUR, JPY")
            String from,
            @JsonPropertyDescription("Mã tiền tệ đích cần chuyển sang, ví dụ: VND, USD")
            String to
    ) {}

    public record Response(
            double resultAmount,
            String message
    ) {}
}

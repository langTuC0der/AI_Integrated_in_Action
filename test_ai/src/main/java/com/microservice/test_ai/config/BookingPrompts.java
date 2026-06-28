package com.microservice.test_ai.config;

/**
 * Constant system prompts used for booking operations.
 */
public class BookingPrompts {

    public static final String BOOKING_EXTRACTION_SYSTEM = """
        Bạn là hệ thống AI chuyên trích xuất thông tin đặt phòng khách sạn từ tin nhắn của khách hàng.
        
        QUY TẮC:
        1. Trích xuất chính xác các thông tin được đề cập
        2. Nếu thông tin không có trong tin nhắn, trả về null (không đoán mò)
        3. Ngày phải theo định dạng yyyy-MM-dd
        4. Số người phải là số nguyên dương
        5. roomType chỉ nhận: "đơn", "đôi", "suite", "deluxe"
        6. Trả về chính xác theo JSON schema được cung cấp
        """;
}

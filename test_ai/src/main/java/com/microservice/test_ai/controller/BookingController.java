package com.microservice.test_ai.controller;

import com.microservice.test_ai.model.BookingRequest;
import com.microservice.test_ai.service.BookingExtractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for the Booking Extraction (ETL with AI) use-case.
 */
@RestController
@RequestMapping("/api/booking")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingExtractionService extractionService;

    public BookingController(BookingExtractionService extractionService) {
        this.extractionService = extractionService;
    }

    /**
     * POST /api/booking/extract
     * Input: { "message": "Booking request in natural language..." }
     * Output: CONFIRMED if complete, or INCOMPLETE with extracted draft fields if missing info.
     */
    @PostMapping("/extract")
    public ResponseEntity<Map<String, Object>> extractAndBook(@RequestBody Map<String, String> request) {
        String customerMessage = request.get("message");

        if (customerMessage == null || customerMessage.isBlank()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Tin nhắn không được trống"));
        }

        // Call AI to perform extraction
        BookingRequest booking = extractionService.extractBookingInfo(customerMessage);

        if (booking == null) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "AI không thể xử lý tin nhắn"));
        }

        // Validate completeness
        if (!booking.isComplete()) {
            return ResponseEntity.ok(Map.of(
                "status", "INCOMPLETE",
                "message", "Vui lòng cung cấp thêm thông tin để hoàn tất đặt phòng",
                "extracted", booking
            ));
        }

        // Success path
        return ResponseEntity.ok(Map.of(
            "status", "CONFIRMED",
            "booking", booking,
            "nights", booking.numberOfNights(),
            "message", "Đặt phòng thành công! Chúng tôi sẽ liên hệ xác nhận sớm."
        ));
    }

    /**
     * POST /api/booking/preview
     * Extracts only (does not apply validation or final status).
     */
    @PostMapping("/preview")
    public ResponseEntity<BookingRequest> previewExtraction(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        BookingRequest booking = extractionService.extractBookingInfo(message);
        return ResponseEntity.ok(booking);
    }
}

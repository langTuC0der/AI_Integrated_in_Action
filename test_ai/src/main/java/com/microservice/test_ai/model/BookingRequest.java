package com.microservice.test_ai.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for hotel booking information extracted by AI.
 */
public record BookingRequest(
    String guestName,           // Guest name
    String roomType,            // Room type: single (đơn), double (đôi), suite, deluxe
    int numberOfGuests,         // Number of guests
    LocalDate checkInDate,      // Check-in date (yyyy-MM-dd)
    LocalDate checkOutDate,     // Check-out date (yyyy-MM-dd)
    List<String> amenities,     // Requested amenities
    String specialRequest       // Special request (nullable)
) {
    // Custom validation method to check if the request is complete
    public boolean isComplete() {
        return guestName != null && !guestName.isBlank()
            && roomType != null && !roomType.isBlank()
            && checkInDate != null
            && checkOutDate != null
            && numberOfGuests > 0;
    }

    // Calculate the number of nights for the booking
    public long numberOfNights() {
        if (checkInDate == null || checkOutDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }
}

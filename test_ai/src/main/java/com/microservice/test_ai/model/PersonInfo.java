package com.microservice.test_ai.model;

/**
 * Data Transfer Object representing personal information extracted from natural text.
 */
public record PersonInfo(
    String name,       // Full name
    int age,           // Age
    String email,      // Email address (if available)
    String phone       // Phone number (if available)
) {}

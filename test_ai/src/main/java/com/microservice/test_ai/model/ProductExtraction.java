package com.microservice.test_ai.model;

/**
 * Data Transfer Object representing product information extracted from orders.
 */
public record ProductExtraction(
    String productName,
    String category,
    double price,
    int quantity,
    String currency,
    boolean inStock
) {}

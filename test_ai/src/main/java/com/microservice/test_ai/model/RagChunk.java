package com.microservice.test_ai.model;

public record RagChunk(
    int id,
    String title,
    String content,
    double score
) {}

package com.microservice.test_ai.controller;

import com.microservice.test_ai.model.RagChunk;
import com.microservice.test_ai.service.Session02RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag/session02")
@CrossOrigin(origins = "*")
public class Session02RagController {

    private final Session02RagService ragService;

    public Session02RagController(Session02RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "document", "Session02-Spring-AI-LLM.md",
            "chunks", ragService.chunkCount()
        );
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> ask(@RequestBody Map<String, String> body) {
        String question = body.get("message");
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message không được trống"));
        }

        return ResponseEntity.ok(ragService.answer(question));
    }

    @PostMapping("/retrieve")
    public ResponseEntity<Map<String, Object>> retrieve(@RequestBody Map<String, String> body) {
        String question = body.get("message");
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message không được trống"));
        }

        List<RagChunk> sources = ragService.retrieve(question);
        return ResponseEntity.ok(Map.of("sources", sources));
    }
}

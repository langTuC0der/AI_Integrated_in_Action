package com.microservice.test_ai.controller;

import com.microservice.test_ai.model.PersonInfo;
import com.microservice.test_ai.model.ProductExtraction;
import com.microservice.test_ai.service.StructuredOutputService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller demonstrating Structured Output conversion from text to Java POJOs/Records.
 */
@RestController
@RequestMapping("/api/extract")
@CrossOrigin(origins = "*")
public class StructuredOutputController {

    private final StructuredOutputService service;

    public StructuredOutputController(StructuredOutputService service) {
        this.service = service;
    }

    /**
     * POST /api/extract/person
     * Request Body: { "text": "natural language containing person details..." }
     * Response: PersonInfo record serialized as JSON.
     */
    @PostMapping("/person")
    public ResponseEntity<PersonInfo> extractPerson(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        PersonInfo result = service.extractPersonInfo(text);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/extract/products
     * Request Body: { "text": "natural language containing order/products..." }
     * Response: List of ProductExtraction records serialized as JSON.
     */
    @PostMapping("/products")
    public ResponseEntity<List<ProductExtraction>> extractProducts(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        List<ProductExtraction> result = service.extractProducts(text);
        return ResponseEntity.ok(result);
    }
}

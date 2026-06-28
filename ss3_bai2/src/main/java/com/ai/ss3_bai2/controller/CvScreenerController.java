package com.ai.ss3_bai2.controller;

import com.ai.ss3_bai2.model.CandidateEvaluation;
import com.ai.ss3_bai2.service.CvScreenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cv")
@RequiredArgsConstructor
public class CvScreenerController {
    private final CvScreenerService cvScreenerService;


    @PostMapping("/evaluate")
    public ResponseEntity<CandidateEvaluation> evaluate(@RequestBody CvEvaluateRequest request) {
        if (request.cvText() == null || request.cvText().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String jd = request.jobDescription() != null ? request.jobDescription() : "Tuyển lập trình viên Java Spring Boot cơ bản";
        CandidateEvaluation evaluation = cvScreenerService.evaluateCV(request.cvText(), jd);
        return ResponseEntity.ok(evaluation);
    }
    public record CvEvaluateRequest(String cvText, String jobDescription) {}
}

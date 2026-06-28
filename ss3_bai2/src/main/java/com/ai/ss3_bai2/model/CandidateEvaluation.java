package com.ai.ss3_bai2.model;


import java.util.List;

public record CandidateEvaluation(
        String candidateName,
        String email,
        String phone,
        List<String> programmingLanguages,
        int yearsOfExperience,
        String education,
        List<ProjectDetail> pastProjects,
        int suitabilityScore, // Điểm số phù hợp từ 1 đến 10
        String matchingAnalysis // Phân tích lý do tại sao đạt điểm số đó
) {
}

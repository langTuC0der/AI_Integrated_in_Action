package com.ai.ss3_bai2.model;

import java.util.List;

public record ProjectDetail(
        String projectName,
        String role,
        List<String> technologies,
        String description
) {
}

package com.learn.project.md3_project.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCriteriaResponse {
    private Long criterionId;
    private String criterionName;
    private String description;
    private BigDecimal maxScore;
    private LocalDateTime createdAt;
}

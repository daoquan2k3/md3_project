package com.learn.project.md3_project.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoundCriteriaResponse {
    private Long roundCriterionId;
    private Long roundId;
    private String roundName;
    private Long criterionId;
    private String criterionName;
    private BigDecimal weight;
    private BigDecimal maxScore;
}

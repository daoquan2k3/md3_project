package com.learn.project.md3_project.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AssessmentResultResponse {
    private Long resultId;
    private Long assignmentId;
    private String studentName;
    private String roundName;
    private String criterionName;
    private BigDecimal score;
    private String comments;
    private String evaluatorName;
    private LocalDateTime evaluationDate;
}

package com.learn.project.md3_project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateAssessmentResultRequest {
    @NotNull(message = "Assignment ID không được để trống")
    private Long assignmentId;

    @NotNull(message = "Round ID không được để trống")
    private Long roundId;

    @NotNull(message = "Criterion ID không được để trống")
    private Long criterionId;

    @NotNull(message = "Điểm số không được để trống")
    private BigDecimal score;

    private String comments;
}

package com.learn.project.md3_project.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateEvaluationCriteriaRequest {
    @NotBlank(message = "Tên tiêu chí không được để trống")
    private String criterionName;
    private String description;
    @NotNull(message = "Điểm tối đa không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Điểm tối đa phải lớn hơn 0")
    private BigDecimal maxScore;
}

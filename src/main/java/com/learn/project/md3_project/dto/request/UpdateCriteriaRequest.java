package com.learn.project.md3_project.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateCriteriaRequest {
    private String criterionName;
    private String description;
    @DecimalMin(value = "0.0", inclusive = false, message = "Điểm tối đa phải lớn hơn 0")
    private BigDecimal maxScore;
}

package com.learn.project.md3_project.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateRoundCriterionRequest {
    @NotNull(message = "Không được bỏ trống!")
    private Long roundId;
    @NotNull(message = "Không được bỏ trống!")
    private Long criterionId;

    @NotNull(message = "Trọng số không được để trống")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    private BigDecimal weight;
}

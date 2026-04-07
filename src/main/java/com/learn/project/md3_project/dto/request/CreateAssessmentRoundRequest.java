package com.learn.project.md3_project.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateAssessmentRoundRequest {
    private Long phaseId;
    private String roundName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private List<CreateRoundCriterionRequest> criteria;
}

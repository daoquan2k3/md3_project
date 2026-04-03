package com.learn.project.md3_project.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateAssessmentRoundRequest {
    private String roundName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}

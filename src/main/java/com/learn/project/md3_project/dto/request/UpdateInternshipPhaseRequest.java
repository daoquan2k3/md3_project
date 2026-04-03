package com.learn.project.md3_project.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateInternshipPhaseRequest {
    private String phaseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Boolean isActive;
}

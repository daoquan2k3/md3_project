package com.learn.project.md3_project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternshipPhaseResponse {
    private Long phaseId;
    private String phaseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Boolean isActive;

    private List<AssessmentRoundResponse> rounds;
}

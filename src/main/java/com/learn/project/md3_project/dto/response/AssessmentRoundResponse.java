package com.learn.project.md3_project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentRoundResponse {
    private Long roundId;
    private String roundName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private List<RoundCriteriaResponse> criteriaList;
}

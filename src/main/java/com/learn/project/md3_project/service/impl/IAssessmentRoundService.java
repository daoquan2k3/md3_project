package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.CreateAssessmentRoundRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssessmentRoundResponse;

import java.util.List;

public interface IAssessmentRoundService {
    ApiResponse<List<AssessmentRoundResponse>> getAllRounds(Long phaseId);
    ApiResponse<AssessmentRoundResponse> getRoundDetail(Long id);
    ApiResponse<AssessmentRoundResponse> createRound(CreateAssessmentRoundRequest dto);
    ApiResponse<AssessmentRoundResponse> updateRound(Long id, CreateAssessmentRoundRequest dto);
    ApiResponse<Void> deleteRound(Long id);

}

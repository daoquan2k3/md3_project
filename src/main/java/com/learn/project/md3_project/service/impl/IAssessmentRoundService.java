package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.CreateAssessmentRoundRequest;
import com.learn.project.md3_project.dto.request.RoundCriterionRequest;
import com.learn.project.md3_project.dto.request.UpdateAssessmentRoundRequest;
import com.learn.project.md3_project.dto.request.UpdateWeightRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssessmentRoundResponse;
import com.learn.project.md3_project.dto.response.RoundCriteriaResponse;

import java.util.List;

public interface IAssessmentRoundService {
    ApiResponse<List<AssessmentRoundResponse>> getAllRounds(Long phaseId);
    ApiResponse<AssessmentRoundResponse> getRoundDetail(Long id);
    ApiResponse<AssessmentRoundResponse> createRound(CreateAssessmentRoundRequest dto);
    ApiResponse<AssessmentRoundResponse> updateRound(Long id, CreateAssessmentRoundRequest dto);
    ApiResponse<Void> deleteRound(Long id);

    ApiResponse<List<RoundCriteriaResponse>> getCriteriaByRound(Long roundId);
    ApiResponse<RoundCriteriaResponse> getCriterionInRound(Long roundId, Long criterionId);
    ApiResponse<Void> addCriterionToRound(Long roundId, RoundCriterionRequest dto);
    ApiResponse<Void> updateCriterionWeight(Long roundId, Long criterionId, UpdateWeightRequest dto);
    ApiResponse<Void> removeCriterionFromRound(Long roundId, Long criterionId);
}

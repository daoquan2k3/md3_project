package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.CreateAssessmentResultRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssessmentResultResponse;

import java.util.List;

public interface IAssessmentResultService {
    ApiResponse<List<AssessmentResultResponse>> getAllResults(Long assignmentId);
    ApiResponse<AssessmentResultResponse> createResult(CreateAssessmentResultRequest dto);
    ApiResponse<AssessmentResultResponse> updateResult(Long resultId, CreateAssessmentResultRequest dto);
}

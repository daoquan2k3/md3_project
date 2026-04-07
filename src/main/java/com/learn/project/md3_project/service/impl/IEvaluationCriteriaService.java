package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.CreateEvaluationCriteriaRequest;
import com.learn.project.md3_project.dto.request.UpdateCriteriaRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.EvaluationCriteriaResponse;

import java.util.List;

public interface IEvaluationCriteriaService {
    ApiResponse<List<EvaluationCriteriaResponse>> getAllCriteria();

    ApiResponse<EvaluationCriteriaResponse> getCriteriaDetail(Long id);

    ApiResponse<EvaluationCriteriaResponse> createCriteria(CreateEvaluationCriteriaRequest dto);

    ApiResponse<EvaluationCriteriaResponse> updateCriteria(Long id, UpdateCriteriaRequest dto);

    ApiResponse<Void> deleteCriteria(Long id);
}

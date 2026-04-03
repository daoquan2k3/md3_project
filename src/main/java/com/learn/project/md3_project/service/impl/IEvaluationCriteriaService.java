package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.EvaluationCriteriaRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.EvaluationCriteriaResponse;

import java.util.List;

public interface IEvaluationCriteriaService {
    ApiResponse<List<EvaluationCriteriaResponse>> getAllCriteria();

    ApiResponse<EvaluationCriteriaResponse> getCriteriaDetail(Long id);

    ApiResponse<EvaluationCriteriaResponse> createCriteria(EvaluationCriteriaRequest dto);

    ApiResponse<EvaluationCriteriaResponse> updateCriteria(Long id, EvaluationCriteriaRequest dto);

    ApiResponse<Void> deleteCriteria(Long id);
}

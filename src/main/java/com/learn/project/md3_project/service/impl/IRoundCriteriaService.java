package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.RoundCriterionRequest;
import com.learn.project.md3_project.dto.request.UpdateWeightRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.RoundCriteriaResponse;
import com.learn.project.md3_project.entity.RoundCriteria;

import java.util.List;

public interface IRoundCriteriaService {
    ApiResponse<List<RoundCriteriaResponse>> getAll(Long roundId);
    ApiResponse<RoundCriteriaResponse> getById(Long id);
    ApiResponse<RoundCriteriaResponse> create(RoundCriterionRequest dto);
    ApiResponse<RoundCriteriaResponse> update(Long id, RoundCriterionRequest dto);
    ApiResponse<Void> delete(Long id);
}

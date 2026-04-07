package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.CreateRoundCriterionRequest;
import com.learn.project.md3_project.dto.request.UpdateRoundCriterionRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.RoundCriteriaResponse;

import java.util.List;

public interface IRoundCriteriaService {
    ApiResponse<List<RoundCriteriaResponse>> getAll(Long roundId);
    ApiResponse<RoundCriteriaResponse> getById(Long id);
    ApiResponse<RoundCriteriaResponse> create(CreateRoundCriterionRequest dto);
    ApiResponse<RoundCriteriaResponse> update(Long id, UpdateRoundCriterionRequest dto);
    ApiResponse<Void> delete(Long id);
}

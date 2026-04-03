package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.CreateInternshipPhaseRequest;
import com.learn.project.md3_project.dto.request.UpdateInternshipPhaseRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.InternshipPhaseResponse;

import java.util.List;

public interface IInternshipPhaseService {
    ApiResponse<List<InternshipPhaseResponse>> getAllPhases();
    ApiResponse<InternshipPhaseResponse> getPhaseDetail(Long phaseId);
    ApiResponse<InternshipPhaseResponse> createPhase(CreateInternshipPhaseRequest dto);
    ApiResponse<InternshipPhaseResponse> updatePhase(Long phaseId, UpdateInternshipPhaseRequest dto);
    ApiResponse<Void> deletePhase(Long phaseId);
}

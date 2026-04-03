package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.AsignStatusUpdateRequest;
import com.learn.project.md3_project.dto.request.AssignmentRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssignmentResponse;

import java.util.List;

public interface IInternshipAssignmentService {
    ApiResponse<AssignmentResponse> createAssignment(AssignmentRequest dto);

    ApiResponse<List<AssignmentResponse>> getAssignmentsByRole();

    ApiResponse<AssignmentResponse> getAssignmentDetail(Long assignmentId);

    ApiResponse<AssignmentResponse> updateAssignmentStatus(Long assignmentId, AsignStatusUpdateRequest dto);

    ApiResponse<?> delete(Long assignmentId);
}

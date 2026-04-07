package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.UpdateInternshipAsStatusRequest;
import com.learn.project.md3_project.dto.request.CreateInternshipAssignmentRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssignmentResponse;

import java.util.List;

public interface IInternshipAssignmentService {
    ApiResponse<AssignmentResponse> createAssignment(CreateInternshipAssignmentRequest dto);

    ApiResponse<List<AssignmentResponse>> getAssignmentsByRole();

    ApiResponse<AssignmentResponse> getAssignmentDetail(Long assignmentId);

    ApiResponse<AssignmentResponse> updateAssignmentStatus(Long assignmentId, UpdateInternshipAsStatusRequest dto);

    ApiResponse<?> delete(Long assignmentId);
}

package com.learn.project.md3_project.controller;

import com.learn.project.md3_project.dto.request.AsignStatusUpdateRequest;
import com.learn.project.md3_project.dto.request.AssignmentRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssignmentResponse;
import com.learn.project.md3_project.service.impl.IInternshipAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internship_assignments")
@RequiredArgsConstructor
public class InternshipAssignmentController {
    private final IInternshipAssignmentService assignmentService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> create(@Valid @RequestBody AssignmentRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.createAssignment(dto));
    }

    @GetMapping("/my-assignments")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getMyAssignments() {
        ApiResponse<List<AssignmentResponse>> response = assignmentService.getAssignmentsByRole();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getAssignmentDetail(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AsignStatusUpdateRequest dto) {

        return ResponseEntity.ok(assignmentService.updateAssignmentStatus(id, dto));
    }
}

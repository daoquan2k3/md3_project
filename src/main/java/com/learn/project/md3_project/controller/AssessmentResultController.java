package com.learn.project.md3_project.controller;

import com.learn.project.md3_project.dto.request.CreateAssessmentResultRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssessmentResultResponse;
import com.learn.project.md3_project.service.impl.IAssessmentResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assessment_results")
@RequiredArgsConstructor
public class AssessmentResultController {
    private final IAssessmentResultService resultService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<List<AssessmentResultResponse>>> getAll(
            @RequestParam(name = "assignment_id", required = false) Long assignmentId) {
        return ResponseEntity.ok(resultService.getAllResults(assignmentId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_MENTOR')")
    public ResponseEntity<ApiResponse<AssessmentResultResponse>> create(
            @Valid @RequestBody CreateAssessmentResultRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resultService.createResult(dto));
    }

    @PutMapping("/{result_id}")
    @PreAuthorize("hasRole('ROLE_MENTOR')")
    public ResponseEntity<ApiResponse<AssessmentResultResponse>> update(
            @PathVariable(name = "result_id") Long resultId,
            @Valid @RequestBody CreateAssessmentResultRequest dto) {
        return ResponseEntity.ok(resultService.updateResult(resultId, dto));
    }
}

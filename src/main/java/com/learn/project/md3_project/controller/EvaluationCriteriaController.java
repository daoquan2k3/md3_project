package com.learn.project.md3_project.controller;

import com.learn.project.md3_project.dto.request.CreateEvaluationCriteriaRequest;
import com.learn.project.md3_project.dto.request.UpdateCriteriaRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.EvaluationCriteriaResponse;
import com.learn.project.md3_project.service.impl.IEvaluationCriteriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluation-criteria")
@RequiredArgsConstructor
public class EvaluationCriteriaController {

    private final IEvaluationCriteriaService criteriaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponse>>> getAll() {
        return ResponseEntity.ok(criteriaService.getAllCriteria());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(criteriaService.getCriteriaDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> create(@Valid @RequestBody CreateEvaluationCriteriaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(criteriaService.createCriteria(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> update(@PathVariable Long id, @Valid @RequestBody UpdateCriteriaRequest dto) {
        return ResponseEntity.ok(criteriaService.updateCriteria(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(criteriaService.deleteCriteria(id));
    }
}

package com.learn.project.md3_project.controller;

import com.learn.project.md3_project.dto.request.CreateAssessmentRoundRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssessmentRoundResponse;
import com.learn.project.md3_project.service.impl.IAssessmentRoundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assessment-rounds")
@RequiredArgsConstructor
public class AssessmentRoundController {
    private final IAssessmentRoundService roundService;

    // Lấy danh sách (Lọc theo phaseId nếu có)
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<List<AssessmentRoundResponse>>> getAll(
            @RequestParam(required = false) Long phaseId) {
        return ResponseEntity.ok(roundService.getAllRounds(phaseId));
    }

    // Lấy chi tiết một vòng
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<AssessmentRoundResponse>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(roundService.getRoundDetail(id));
    }

    // Tạo mới vòng kèm tiêu chí
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AssessmentRoundResponse>> create(
            @Valid @RequestBody CreateAssessmentRoundRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roundService.createRound(dto));
    }

    // Cập nhật vòng và danh sách tiêu chí
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AssessmentRoundResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateAssessmentRoundRequest dto) {
        return ResponseEntity.ok(roundService.updateRound(id, dto));
    }

    // Xóa vòng
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(roundService.deleteRound(id));
    }
}

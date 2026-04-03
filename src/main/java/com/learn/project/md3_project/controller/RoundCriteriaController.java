package com.learn.project.md3_project.controller;

import com.learn.project.md3_project.dto.request.RoundCriterionRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.RoundCriteriaResponse;
import com.learn.project.md3_project.service.impl.IRoundCriteriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/round_criteria")
@RequiredArgsConstructor
public class RoundCriteriaController {
    private final IRoundCriteriaService roundCriteriaService;

    // 1. Lấy danh sách (Có thể dùng query param ?round_id=... để lọc)
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<List<RoundCriteriaResponse>>> getAll(
            @RequestParam(name = "round_id", required = false) Long roundId) {
        return ResponseEntity.ok(roundCriteriaService.getAll(roundId));
    }

    // 2. Lấy chi tiết
    @GetMapping("/{round_criterion_id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<RoundCriteriaResponse>> getById(
            @PathVariable(name = "round_criterion_id") Long id) {
        return ResponseEntity.ok(roundCriteriaService.getById(id));
    }

    // 3. Thêm mới
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RoundCriteriaResponse>> create(
            @Valid @RequestBody RoundCriterionRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roundCriteriaService.create(dto));
    }

    // 4. Cập nhật trọng số
    @PutMapping("/{round_criterion_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RoundCriteriaResponse>> update(
            @PathVariable(name = "round_criterion_id") Long id,
            @Valid @RequestBody RoundCriterionRequest dto) {
        return ResponseEntity.ok(roundCriteriaService.update(id, dto));
    }

    // 5. Xóa
    @DeleteMapping("/{round_criterion_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable(name = "round_criterion_id") Long id) {
        return ResponseEntity.ok(roundCriteriaService.delete(id));
    }
}

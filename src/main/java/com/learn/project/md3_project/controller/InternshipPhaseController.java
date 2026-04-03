package com.learn.project.md3_project.controller;

import com.learn.project.md3_project.dto.request.CreateInternshipPhaseRequest;
import com.learn.project.md3_project.dto.request.UpdateInternshipPhaseRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.InternshipPhaseResponse;
import com.learn.project.md3_project.service.impl.IInternshipPhaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/phases")
@RequiredArgsConstructor
public class InternshipPhaseController {
    private final IInternshipPhaseService phaseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<List<InternshipPhaseResponse>>> getAllPhases() {
        return ResponseEntity.ok(phaseService.getAllPhases());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<InternshipPhaseResponse>> getPhaseDetail(@PathVariable Long id) {
        return ResponseEntity.ok(phaseService.getPhaseDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<InternshipPhaseResponse>> createPhase(
            @Valid @RequestBody CreateInternshipPhaseRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(phaseService.createPhase(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<InternshipPhaseResponse>> updatePhase(
            @PathVariable Long id,
            @RequestBody UpdateInternshipPhaseRequest dto) {
        return ResponseEntity.ok(phaseService.updatePhase(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePhase(@PathVariable Long id) {
        return ResponseEntity.ok(phaseService.deletePhase(id));
    }
}

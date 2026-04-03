package com.learn.project.md3_project.controller;

import com.learn.project.md3_project.dto.request.CreateMentorRequest;
import com.learn.project.md3_project.dto.request.UpdateMentorRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.MentorResponse;
import com.learn.project.md3_project.service.impl.IMentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mentors")
@RequiredArgsConstructor
public class MentorController {
    private final IMentorService mentorService;

    @GetMapping//lấy danh sách giảng viên-tất cả giảng viên phụ trách của 1 học sinh
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<List<MentorResponse>>> getAllMentors() {
        return ResponseEntity.ok(mentorService.getAllMentorsByRole());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<MentorResponse>> getMentorDetail(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.getMentorDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<MentorResponse>> createMentor(
            @Valid @RequestBody CreateMentorRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorService.createMentor(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR')")
    public ResponseEntity<ApiResponse<MentorResponse>> updateMentor(
            @PathVariable Long id,
            @RequestBody UpdateMentorRequest dto) {
        return ResponseEntity.ok(mentorService.updateMentor(id, dto));
    }
}

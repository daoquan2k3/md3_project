package com.learn.project.md3_project.controller;

import com.learn.project.md3_project.dto.request.CreateStudentRequest;
import com.learn.project.md3_project.dto.request.UpdateStudentRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.StudentResponse;
import com.learn.project.md3_project.service.impl.IStudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {
    private final IStudentService iStudentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {
        return ResponseEntity.ok(iStudentService.getAllStudentsByRole());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MENTOR', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentDetail(@PathVariable Long id) {
        return ResponseEntity.ok(iStudentService.getStudentDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody CreateStudentRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iStudentService.createStudent(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long id,
            @RequestBody UpdateStudentRequest dto) {
        return ResponseEntity.ok(iStudentService.updateStudent(id, dto));
    }
}

package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.CreateStudentRequest;
import com.learn.project.md3_project.dto.request.UpdateStudentRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.StudentResponse;

import java.util.List;

public interface IStudentService {
    ApiResponse<List<StudentResponse>> getAllStudentsByRole();

    ApiResponse<StudentResponse> getStudentDetail(Long studentId);

    ApiResponse<StudentResponse> createStudent(CreateStudentRequest dto);

    ApiResponse<StudentResponse> updateStudent(Long studentId, UpdateStudentRequest dto);
}

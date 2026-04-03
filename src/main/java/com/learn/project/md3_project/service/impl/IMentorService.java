package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.CreateMentorRequest;
import com.learn.project.md3_project.dto.request.UpdateMentorRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.MentorResponse;

import java.util.List;

public interface IMentorService {
    ApiResponse<List<MentorResponse>> getAllMentorsByRole();
    ApiResponse<MentorResponse> getMentorDetail(Long mentorId);
    ApiResponse<MentorResponse> createMentor(CreateMentorRequest dto);
    ApiResponse<MentorResponse> updateMentor(Long mentorId, UpdateMentorRequest dto);
}

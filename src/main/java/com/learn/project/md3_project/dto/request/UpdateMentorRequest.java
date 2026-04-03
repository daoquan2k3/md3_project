package com.learn.project.md3_project.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMentorRequest {
    private String fullName;
    private String phone;

    // Thông tin bảng Mentors
    private String department;
    private String academicRank;
}

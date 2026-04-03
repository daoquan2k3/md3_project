package com.learn.project.md3_project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MentorResponse {
    private Long mentorId;

    // Thông tin từ bảng User
    private String fullName;
    private String email;
    private String phone;
    private Boolean isActive;

    // Thông tin đặc thù của Mentor
    private String department;
    private String academicRank;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

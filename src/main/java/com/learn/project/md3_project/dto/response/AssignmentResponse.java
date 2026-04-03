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
public class AssignmentResponse {
    private Long assignmentId;

    // Thông tin Sinh viên
    private Long studentId;
    private String studentName;
    private String studentCode;

    // Thông tin Giảng viên
    private Long mentorId;
    private String mentorName;

    // Thông tin Giai đoạn
    private Long phaseId;
    private String phaseName;

    private String status;
    private LocalDateTime assignedDate;
}

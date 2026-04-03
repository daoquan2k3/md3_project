package com.learn.project.md3_project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentRequest {
    @NotNull(message = "ID sinh viên không được để trống")
    private Long studentId;

    @NotNull(message = "ID giảng viên không được để trống")
    private Long mentorId;

    @NotNull(message = "ID giai đoạn không được để trống")
    private Long phaseId;
}

package com.learn.project.md3_project.dto.request;

import com.learn.project.md3_project.entity.InternshipAssignment;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInternshipAsStatusRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private InternshipAssignment.AssignmentStatus status;
}

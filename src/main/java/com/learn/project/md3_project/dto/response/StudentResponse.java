package com.learn.project.md3_project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentResponse {
    private Long studentId;
    private String studentCode;
    private String fullName;
    private String email;
    private String phone;
    private String major;
    private String studentClass;
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isActive;
}

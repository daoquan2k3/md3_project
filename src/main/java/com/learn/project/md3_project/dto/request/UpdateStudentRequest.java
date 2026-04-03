package com.learn.project.md3_project.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateStudentRequest {
    private String fullName;
    @Pattern(regexp = "^(0|84)(3|5|7|8|9)([0-9]{8})$")
    private String phone;
    private String major;
    private String studentClass;
    @Past(message = "Ngày sinh không hợp lệ")
    private LocalDate dateOfBirth;
    private String address;
}

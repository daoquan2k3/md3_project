package com.learn.project.md3_project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateStudentRequest {
    @NotEmpty(message = "Không được để trống")
    private String fullName;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|84)(3|5|7|8|9)([0-9]{8})$")
    private String phoneNumber;
    @NotEmpty(message = "Không được để trống")
    private String major;
    @NotEmpty(message = "Không được để trống")
    private String studentClass;
    @Past(message = "Ngày sinh không hợp lệ")
    private LocalDate dateOfBirth;
    @NotEmpty(message = "Không được để trống")
    private String address;
}

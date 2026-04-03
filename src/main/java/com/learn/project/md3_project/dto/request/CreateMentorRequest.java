package com.learn.project.md3_project.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMentorRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String passwordHash;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @Pattern(regexp = "^(0|84)(3|5|7|8|9)([0-9]{8})$")
    private String phone;

    @NotBlank(message = "Khoa/Bộ môn không được để trống")
    private String department;

    private String academicRank;
}

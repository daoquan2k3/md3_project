package com.learn.project.md3_project.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateStudentRequest {
    private String username;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Định dạng email không hợp lệ")
    private String email;
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    private String passwordHash;
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    // Thông tin sinh viên (Student)
    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentCode;
    private String major;
    private String studentClass;
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    private LocalDate dateOfBirth;
    private String address;
}

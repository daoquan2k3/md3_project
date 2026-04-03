package com.learn.project.md3_project.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Username không được để trống")
    private String username;
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;
    @Email
    @NotBlank(message = "Email không được để trống")
    private String email;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|84)(3|5|7|8|9)([0-9]{8})$")
    private String phone;
    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    private String passwordHash;
    @NotEmpty(message = "Quyền không được để trống")
    private List<String> roles;
}

package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.LoginRequest;
import com.learn.project.md3_project.dto.request.RegisterRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.JwtResponse;
import com.learn.project.md3_project.entity.Role;

import java.util.List;
import java.util.Set;

public interface IAuthService {
    void register(RegisterRequest dto);
    JwtResponse login(LoginRequest dto);
    Set<Role> mapRoles(List<String> roles);
    void loginStep1(LoginRequest dto);
    JwtResponse loginStep2(String email, String otp);
}

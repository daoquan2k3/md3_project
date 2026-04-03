package com.learn.project.md3_project.controller;

import com.learn.project.md3_project.dto.request.LoginRequest;
import com.learn.project.md3_project.dto.request.RegisterRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.JwtResponse;
import com.learn.project.md3_project.service.impl.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterRequest dto) {
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Đăng ký tài khoản thành công"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginRequest dto) {
        authService.loginStep1(dto);
        return ResponseEntity.ok(ApiResponse.success(null, "Mã OTP đã được gửi về Email của bạn."));
    }

    // Bước 2: Nhập OTP để lấy JWT
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<JwtResponse>> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        JwtResponse response = authService.loginStep2(email, otp);
        return ResponseEntity.ok(ApiResponse.success(response, "Đăng nhập thành công"));
    }
}
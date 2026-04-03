package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.LoginRequest;
import com.learn.project.md3_project.dto.request.RegisterRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.JwtResponse;
import com.learn.project.md3_project.entity.Role;
import com.learn.project.md3_project.entity.RoleName;
import com.learn.project.md3_project.entity.User;
import com.learn.project.md3_project.exception.DataExistException;
import com.learn.project.md3_project.exception.ResourceNotFoundException;
import com.learn.project.md3_project.repository.IRoleRepository;
import com.learn.project.md3_project.repository.IUserRepository;
import com.learn.project.md3_project.security.jwt.JwtProvider;
import com.learn.project.md3_project.security.principle.UserDetailCustom;
import com.learn.project.md3_project.service.impl.IAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements IAuthService {
    private final IRoleRepository iRoleRepository;
    private final IUserRepository iUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final OtpService otpService;

    @Override
    public void register(RegisterRequest dto) {
        log.info("Bắt đầu quá trình đăng ký cho email: {}", dto.getEmail());

        if (iUserRepository.existsByEmail(dto.getEmail())) {
            log.warn("Đăng ký thất bại: Email {} đã tồn tại", dto.getEmail());
            throw new DataExistException("Email đã tồn tại!");
        }
        try {
            User user = modelMapper.map(dto, User.class);
            user.setPasswordHash(passwordEncoder.encode(dto.getPasswordHash()));
            user.setRoles(mapRoles(dto.getRoles()));
            iUserRepository.save(user);
            log.info("Lưu người dùng mới vào database thành công");
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi lưu User: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public JwtResponse login(LoginRequest dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPasswordHash())
        );

        UserDetailCustom userDetails = (UserDetailCustom) authentication.getPrincipal();

        String accessToken = jwtProvider.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtProvider.generateRefreshToken(userDetails.getUsername());

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(userDetails.getUsername())
                .roles(roles)
                .build();
    }

    @Override
    public Set<Role> mapRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of(iRoleRepository.findByRoleName(RoleName.ROLE_STUDENT).orElseThrow());
        }
        return roles.stream()
                .map(r -> {
                    String roleName = r.startsWith("ROLE_") ? r.toUpperCase() : "ROLE_" + r.toUpperCase();
                    return iRoleRepository.findByRoleName(RoleName.valueOf(roleName))
                            .orElseThrow(() -> new RuntimeException("Quyền " + r + " không tồn tại"));
                })
                .collect(Collectors.toSet());
    }

    @Override
    public void loginStep1(LoginRequest dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPasswordHash())
        );

        String otp = otpService.generateOtp(dto.getEmail());
        emailService.sendOtpEmail(dto.getEmail(), otp);
        log.info("OTP gửi tới {}: {}", dto.getEmail(), otp);
    }

    @Override
    public JwtResponse loginStep2(String email, String otp) {
        if (!otpService.validateOtp(email, otp)) {
            throw new RuntimeException("Mã OTP không chính xác hoặc đã hết hạn");
        }

        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        return JwtResponse.builder()
                .accessToken(jwtProvider.generateAccessToken(user.getEmail()))
                .refreshToken(jwtProvider.generateRefreshToken(user.getEmail()))
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(r -> r.getRoleName().name()).collect(Collectors.toSet()))
                .build();
    }
}

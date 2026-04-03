package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.UpdateUserRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.MentorResponse;
import com.learn.project.md3_project.dto.response.StudentResponse;
import com.learn.project.md3_project.dto.response.UserResponse;
import com.learn.project.md3_project.entity.Role;
import com.learn.project.md3_project.entity.RoleName;
import com.learn.project.md3_project.entity.User;
import com.learn.project.md3_project.exception.ResourceNotFoundException;
import com.learn.project.md3_project.repository.IMentorRepository;
import com.learn.project.md3_project.repository.IRoleRepository;
import com.learn.project.md3_project.repository.IStudentRepository;
import com.learn.project.md3_project.repository.IUserRepository;
import com.learn.project.md3_project.security.principle.UserDetailCustom;
import com.learn.project.md3_project.service.impl.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {
    private final IUserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final IStudentRepository iStudentRepository;
    private final IMentorRepository iMentorRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<UserResponse> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Phiên đăng nhập không hợp lệ hoặc người dùng không tồn tại"));

        UserResponse response = modelMapper.map(user, UserResponse.class);
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet()));

        if (response.getRoles().contains("ROLE_MENTOR")) {
            iMentorRepository.findById(user.getUserId())
                    .ifPresent(mentor -> response.setDetail(modelMapper.map(mentor, MentorResponse.class)));
        }
        else if (response.getRoles().contains("ROLE_STUDENT")) {
            iStudentRepository.findById(user.getUserId())
                    .ifPresent(student -> response.setDetail(modelMapper.map(student, StudentResponse.class)));
        }

        return ApiResponse.success(response, "Lấy thông tin cá nhân thành công");
    }

    @Override
    @Transactional
    public ApiResponse<UserResponse> changeUserRole(Long userId, Set<String> roleNames) {
        User targetUser = iUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ID: " + userId));

        boolean isTargetAdmin = targetUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(RoleName.ROLE_ADMIN));

        if (isTargetAdmin) {
            throw new RuntimeException("Vi phạm bảo mật: Không thể thay đổi quyền của một quản trị viên khác!");
        }

        log.info("Đang thay đổi quyền cho User ID: {} sang {}", userId, roleNames);

        Set<Role> newRoles = roleNames.stream()
                .map(name -> {
                    String formattedName = name.startsWith("ROLE_") ? name.toUpperCase() : "ROLE_" + name.toUpperCase();
                    return iRoleRepository.findByRoleName(RoleName.valueOf(formattedName))
                            .orElseThrow(() -> new RuntimeException("Quyền " + name + " không tồn tại"));
                })
                .collect(Collectors.toSet());

        targetUser.setRoles(newRoles);
        User updatedUser = iUserRepository.save(targetUser);

        UserResponse response = modelMapper.map(updatedUser, UserResponse.class);
        response.setRoles(updatedUser.getRoles().stream()
                .map(r -> r.getRoleName().name()).collect(Collectors.toSet()));

        return ApiResponse.success(response, "Cập nhật quyền thành công");
    }

    @Override
    public ApiResponse<List<UserResponse>> getAllUserByRoles(Set<String> roleNames) {
        log.info("Bắt đầu lấy danh sách User theo roles: {}", roleNames);

        Set<RoleName> enumRoles = roleNames.stream()
                .map(name -> {
                    String formatted = name.startsWith("ROLE_") ? name.toUpperCase() : "ROLE_" + name.toUpperCase();
                    return RoleName.valueOf(formatted);
                })
                .collect(Collectors.toSet());

        List<User> users = iUserRepository.findDistinctByRoles_RoleNameIn(enumRoles);

        List<UserResponse> responses = users.stream()
                .map(user -> {
                    UserResponse dto = modelMapper.map(user, UserResponse.class);

                    dto.setRoles(user.getRoles().stream()
                            .map(role -> role.getRoleName().name())
                            .collect(Collectors.toSet()));

                    dto.setPhoneNumber(user.getPhoneNumber());

                    return dto;
                })
                .collect(Collectors.toList());

        return ApiResponse.success(responses, "Lấy danh sách người dùng theo quyền thành công");
    }

    @Override
    public ApiResponse<UserResponse> getUserById(Long id) {
        log.info("Đang tìm kiếm người dùng với ID: {}", id);

        User user = iUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        userResponse.setRoles(user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet()));

        userResponse.setPhoneNumber(user.getPhoneNumber());

        return ApiResponse.success(userResponse, "Lấy thông tin người dùng thành công");
    }

    @Override
    public ApiResponse<UserResponse> updateUser(UpdateUserRequest dto, Long id) {
        log.info("Đang cập nhật thông tin cho User ID: {}", id);

        User user = iUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ID: " + id));


        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());

        User updatedUser = iUserRepository.save(user);

        UserResponse response = modelMapper.map(updatedUser, UserResponse.class);
        response.setRoles(updatedUser.getRoles().stream()
                .map(r -> r.getRoleName().name()).collect(Collectors.toSet()));

        if (response.getRoles().contains("ROLE_STUDENT")) {
            iStudentRepository.findById(id).ifPresent(s ->
                    response.setDetail(modelMapper.map(s, StudentResponse.class)));
        } else if (response.getRoles().contains("ROLE_MENTOR")) {
            iMentorRepository.findById(id).ifPresent(m ->
                    response.setDetail(modelMapper.map(m, MentorResponse.class)));
        }

        return ApiResponse.success(response, "Cập nhật thông tin thành công");
    }

    @Override
    public ApiResponse<UserResponse> updateStatus(Long id, Boolean isActive) {
        log.info("ADMIN đang thay đổi trạng thái User ID: {} thành {}", id, isActive);

        User user = iUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ID: " + id));

        user.setIsActive(isActive);
        User updatedUser = iUserRepository.save(user);

        UserResponse response = modelMapper.map(updatedUser, UserResponse.class);
        response.setRoles(updatedUser.getRoles().stream()
                .map(r -> r.getRoleName().name()).collect(Collectors.toSet()));

        String statusMsg = isActive ? "Kích hoạt tài khoản thành công" : "Khóa tài khoản thành công";
        return ApiResponse.success(response, statusMsg);
    }

    @Override
    @Transactional
    public ApiResponse<Object> softDeleteUser(Long id) {
        User user = iUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ID: " + id));

        user.setIsActive(false);
        user.setIsDeleted(true);
        iUserRepository.save(user);

        return ApiResponse.success(null, "Đã tạm khóa và đánh dấu xóa người dùng");
    }

    @Override
    public ApiResponse<Object> deleteUser(Long id) {
        log.info("ADMIN đang xóa vĩnh viễn User ID: {}", id);

        User user = iUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ID: " + id));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getRoleName().equals(RoleName.ROLE_ADMIN));
        if (isAdmin) {
            throw new RuntimeException("Không thể xóa tài khoản Quản trị viên!");
        }

        iUserRepository.delete(user);

        return ApiResponse.success(null, "Đã xóa người dùng khỏi hệ thống vĩnh viễn");
    }
}
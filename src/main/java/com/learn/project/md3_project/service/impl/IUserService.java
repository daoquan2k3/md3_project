package com.learn.project.md3_project.service.impl;

import com.learn.project.md3_project.dto.request.UpdateUserRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.UserResponse;
import com.learn.project.md3_project.entity.User;

import java.util.List;
import java.util.Set;

public interface IUserService {
    ApiResponse<UserResponse> getCurrentUser();
    ApiResponse<UserResponse> changeUserRole(Long userId, Set<String> roleNames);
    ApiResponse<List<UserResponse>> getAllUserByRoles(Set<String> roleNames);
    ApiResponse<UserResponse> getUserById(Long id);
    ApiResponse<UserResponse> updateUser(UpdateUserRequest dto, Long id);
    ApiResponse<UserResponse> updateStatus(Long id, Boolean isActive);
    //xóa mềm
    ApiResponse<Object> softDeleteUser(Long id);
    //xóa cứng
    ApiResponse<Object> deleteUser(Long id);
}

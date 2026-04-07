package com.learn.project.md3_project.controller;

import com.learn.project.md3_project.dto.request.UpdateRoleRequest;
import com.learn.project.md3_project.dto.request.UpdateUserRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.UserResponse;
import com.learn.project.md3_project.entity.User;
import com.learn.project.md3_project.service.impl.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> changeRoles(
            @PathVariable Long id,
            @RequestBody Set<String> roleNames) {

        return ResponseEntity.ok(userService.changeUserRole(id, roleNames));
    }

    @GetMapping("/by-roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRoles(
            @RequestParam Set<String> roleNames) {
        return ResponseEntity.ok(userService.getAllUserByRoles(roleNames));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.name == #dto.email")
    public ResponseEntity<ApiResponse<UserResponse>> updateInfo(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest dto) {

        return ResponseEntity.ok(userService.updateUser(dto, id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> changeUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {

        return ResponseEntity.ok(userService.updateStatus(id, isActive));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") //xóa mềm
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.softDeleteUser(id));
    }


}

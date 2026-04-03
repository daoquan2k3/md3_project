package com.learn.project.md3_project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    //private String passwordHash;
    private String phoneNumber;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private Boolean isActive;

    private Object detail;
}

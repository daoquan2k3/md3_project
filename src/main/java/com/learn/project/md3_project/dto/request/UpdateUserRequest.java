package com.learn.project.md3_project.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
}

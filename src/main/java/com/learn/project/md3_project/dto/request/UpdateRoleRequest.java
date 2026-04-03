package com.learn.project.md3_project.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UpdateRoleRequest {
    private Set<String> roles;
}

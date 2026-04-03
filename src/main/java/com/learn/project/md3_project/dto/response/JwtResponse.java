package com.learn.project.md3_project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtResponse {
    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
    private Date expired;
    private Long id;
    private String email;
    private String phone;
    private Set<String> roles;
}

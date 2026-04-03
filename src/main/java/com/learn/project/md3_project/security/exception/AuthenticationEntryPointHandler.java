package com.learn.project.md3_project.security.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // Code : 401
        // message : un authentication - mô tả rõ ai sai quyền truy cập
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 403
        response.setContentType("application/json");

        String ex = (String) request.getAttribute("exception");
        Map<String, Object> data = new HashMap<>();
        data.put("status", 401);
        if("ExpiredJwtException".equals(ex)){
            data.put("message", "Expired Jwt");

        }else {
            data.put("message", 401);
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(data);

        response.getWriter().write(json);
        response.getWriter().flush();
        response.getWriter().close();
    }
}


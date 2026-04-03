package com.learn.project.md3_project.security.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Map<String, Object> data = new HashMap<>();
        data.put("status", HttpServletResponse.SC_FORBIDDEN);
        data.put("message", "ACCESS DENIED");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(data);

        response.getWriter().write(json);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
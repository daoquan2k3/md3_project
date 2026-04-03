package com.learn.project.md3_project.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseCustomException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND); // Trả về 404 Not Found
    }
}

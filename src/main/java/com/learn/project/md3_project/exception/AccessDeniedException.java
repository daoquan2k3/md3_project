package com.learn.project.md3_project.exception;

import org.springframework.http.HttpStatus;


public class AccessDeniedException extends BaseCustomException{
    public AccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}

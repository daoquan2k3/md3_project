package com.learn.project.md3_project.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseCustomException extends RuntimeException {
    private final HttpStatus status;

    public BaseCustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}

package com.learn.project.md3_project.exception;

import org.springframework.http.HttpStatus;

public class DataExistException extends BaseCustomException {
    public DataExistException(String message) {
        super(message, HttpStatus.CONFLICT); // Trả về 409 Conflict
    }
}

package com.skillsync.session.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedSessionAccessException extends RuntimeException {
    public UnauthorizedSessionAccessException(String msg) {
        super(msg);
    }
}
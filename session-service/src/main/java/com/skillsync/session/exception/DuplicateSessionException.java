package com.skillsync.session.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateSessionException extends RuntimeException {
    public DuplicateSessionException(String msg) {
        super(msg);
    }
}
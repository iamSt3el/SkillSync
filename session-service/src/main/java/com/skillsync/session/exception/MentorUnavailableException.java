package com.skillsync.session.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MentorUnavailableException extends RuntimeException {
    public MentorUnavailableException(String msg) {
        super(msg);
    }
}
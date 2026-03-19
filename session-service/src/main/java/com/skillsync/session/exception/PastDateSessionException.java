package com.skillsync.session.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PastDateSessionException extends RuntimeException{
	public PastDateSessionException(String msg) {
		super(msg);
	}
}

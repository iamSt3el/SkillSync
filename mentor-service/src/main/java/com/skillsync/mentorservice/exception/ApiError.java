package com.skillsync.mentorservice.exception;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder
public class ApiError {
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}

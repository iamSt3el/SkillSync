package com.skillsync.mentorservice.Messaging;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MentorAppliedEvent {

    private Long mentorId;
    private Long userId;
    private String bio;
    private Integer experience;
    private Double hourlyRate;
    private LocalDateTime appliedAt;
    
}

package com.skillsync.mentorservice.Messaging;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MentorApprovedEvent {

    private Long mentorId;
    private Long userId;
    private LocalDateTime approvedAt;
}

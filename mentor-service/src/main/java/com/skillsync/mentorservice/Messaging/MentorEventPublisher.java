package com.skillsync.mentorservice.Messaging;

import com.skillsync.mentorservice.Messaging.RabbitMQConfig;
import com.skillsync.mentorservice.Messaging.MentorAppliedEvent;
import com.skillsync.mentorservice.Messaging.MentorApprovedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    // Called when a mentor applies
    public void publishMentorApplied(MentorAppliedEvent event) {
        log.info("Publishing MentorAppliedEvent for userId: {}", event.getUserId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.MENTOR_EXCHANGE,
                RabbitMQConfig.MENTOR_APPLIED_ROUTING_KEY,
                event
        );
        log.info("MentorAppliedEvent published successfully for userId: {}", event.getUserId());
    }

    // Called when admin approves a mentor
    public void publishMentorApproved(MentorApprovedEvent event) {
        log.info("Publishing MentorApprovedEvent for mentorId: {}", event.getMentorId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.MENTOR_EXCHANGE,
                RabbitMQConfig.MENTOR_APPROVED_ROUTING_KEY,
                event
        );
        log.info("MentorApprovedEvent published successfully for mentorId: {}", event.getMentorId());
    }
}

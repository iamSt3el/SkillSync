package com.skillsync.sessionservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillsync.sessionservice.config.MentorClient;
import com.skillsync.sessionservice.config.RabbitMQConfig;
import com.skillsync.sessionservice.dto.request.SessionBookRequest;
import com.skillsync.sessionservice.dto.response.SessionResponse;
import com.skillsync.sessionservice.entity.Session;
import com.skillsync.sessionservice.entity.SessionStatus;
import com.skillsync.sessionservice.event.SessionEvent;
import com.skillsync.sessionservice.exception.InvalidSessionStateException;
import com.skillsync.sessionservice.exception.MentorNotFoundException;
import com.skillsync.sessionservice.exception.ServiceUnavailableException;
import com.skillsync.sessionservice.exception.SessionNotFoundException;
import com.skillsync.sessionservice.mapper.SessionMapper;
import com.skillsync.sessionservice.repository.SessionRepository;
import com.skillsync.sessionservice.service.SessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final RabbitTemplate rabbitTemplate;
    private final MentorClient mentorClient;

    // ─────────────────────────────────────────────
    // BOOK SESSION
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public SessionResponse bookSession(SessionBookRequest request) {

        validateMentor(request.getMentorId());

        Session session = sessionMapper.toEntity(request);
        Session saved = sessionRepository.save(session);

        publishSessionEvent(saved, RabbitMQConfig.SESSION_BOOKED_KEY);

        return sessionMapper.toResponse(saved);
    }

    // ─────────────────────────────────────────────
    // ACCEPT SESSION
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public SessionResponse acceptSession(Long sessionId) {

        Session session = findSessionOrThrow(sessionId);

        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new InvalidSessionStateException("Only REQUESTED can be accepted");
        }

        session.setStatus(SessionStatus.ACCEPTED);
        Session updated = sessionRepository.save(session);

        publishSessionEvent(updated, RabbitMQConfig.SESSION_ACCEPTED_KEY);

        return sessionMapper.toResponse(updated);
    }

    // ─────────────────────────────────────────────
    // REJECT SESSION
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public SessionResponse rejectSession(Long sessionId) {

        Session session = findSessionOrThrow(sessionId);

        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new InvalidSessionStateException("Only REQUESTED can be rejected");
        }

        session.setStatus(SessionStatus.REJECTED);
        Session updated = sessionRepository.save(session);

        publishSessionEvent(updated, RabbitMQConfig.SESSION_REJECTED_KEY);

        return sessionMapper.toResponse(updated);
    }

    // ─────────────────────────────────────────────
    // CANCEL SESSION
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public SessionResponse cancelSession(Long sessionId) {

        Session session = findSessionOrThrow(sessionId);

        if (session.getStatus() == SessionStatus.COMPLETED ||
            session.getStatus() == SessionStatus.CANCELLED) {

            throw new InvalidSessionStateException("Cannot cancel completed/cancelled session");
        }

        session.setStatus(SessionStatus.CANCELLED);
        Session updated = sessionRepository.save(session);

        publishSessionEvent(updated, RabbitMQConfig.SESSION_CANCELLED_KEY);

        return sessionMapper.toResponse(updated);
    }

    // ─────────────────────────────────────────────
    // GET USER SESSIONS
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<SessionResponse> getSessionsByUserId(Long userId) {

        return sessionRepository
                .findByLearnerIdOrMentorId(userId, userId)
                .stream()
                .map(sessionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // COMMON METHODS
    // ─────────────────────────────────────────────

    private Session findSessionOrThrow(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException("Session not found: " + id));
    }

    private void validateMentor(Long mentorId) {
        try {
            Boolean exists = mentorClient.mentorExists(mentorId);

            if (!Boolean.TRUE.equals(exists)) {
                throw new MentorNotFoundException("Mentor not found: " + mentorId);
            }

        } catch (MentorNotFoundException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new ServiceUnavailableException("Mentor service unavailable");
        }
    }

    private void publishSessionEvent(Session session, String routingKey) {
        try {
            SessionEvent event = SessionEvent.builder()
                    .sessionId(session.getId())
                    .mentorId(session.getMentorId())
                    .learnerId(session.getLearnerId())
                    .sessionTime(session.getSessionDate())
                    .topic(session.getTopic())
                    .eventType(routingKey)
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.SESSION_EXCHANGE,
                    routingKey,
                    event
            );

            log.info("Event published: {} for sessionId={}", routingKey, session.getId());

        } catch (Exception ex) {
            log.error("Event publish failed", ex);
            throw new RuntimeException("Event publishing failed");
        }
    }
}
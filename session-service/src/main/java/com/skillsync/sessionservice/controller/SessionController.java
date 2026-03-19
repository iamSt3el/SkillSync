package com.skillsync.sessionservice.controller;

import com.skillsync.sessionservice.dto.request.SessionBookRequest;
import com.skillsync.sessionservice.dto.response.SessionResponse;
import com.skillsync.sessionservice.entity.Session;
import com.skillsync.sessionservice.exception.SessionNotFoundException;
import com.skillsync.sessionservice.repository.SessionRepository;
import com.skillsync.sessionservice.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final SessionRepository sessionRepository;

    /**
     * POST /sessions
     * Learner books a new session with a mentor. Status starts as REQUESTED.
     */
    @PostMapping
    public ResponseEntity<SessionResponse> bookSession(@Valid @RequestBody SessionBookRequest request) {
        log.info("POST /sessions - booking session");
        SessionResponse response = sessionService.bookSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /sessions/{id}/accept
     * Mentor accepts a REQUESTED session → ACCEPTED.
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<SessionResponse> acceptSession(@PathVariable Long id) {
        log.info("PUT /sessions/{}/accept", id);
        return ResponseEntity.ok(sessionService.acceptSession(id));
    }

    /**
     * PUT /sessions/{id}/reject
     * Mentor rejects a REQUESTED session → REJECTED.
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<SessionResponse> rejectSession(@PathVariable Long id) {
        log.info("PUT /sessions/{}/reject", id);
        return ResponseEntity.ok(sessionService.rejectSession(id));
    }

    /**
     * PUT /sessions/{id}/cancel
     * Learner or Mentor cancels a REQUESTED/ACCEPTED session → CANCELLED.
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<SessionResponse> cancelSession(@PathVariable Long id) {
        log.info("PUT /sessions/{}/cancel", id);
        return ResponseEntity.ok(sessionService.cancelSession(id));
    }

    /**
     * GET /sessions/user/{userId}
     * Returns all sessions where the user is either the learner or the mentor.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SessionResponse>> getSessionsByUserId(@PathVariable Long userId) {
        log.info("GET /sessions/user/{}", userId);
        return ResponseEntity.ok(sessionService.getSessionsByUserId(userId));
    }

    /**
     * GET /sessions/{sessionId}/status
     * Internal endpoint consumed by Review Service via Feign.
     * Validates that a session is COMPLETED before a review is allowed.
     */
    @GetMapping("/{sessionId}/status")
    public ResponseEntity<String> getSessionStatus(@PathVariable Long sessionId) {
        log.info("GET /sessions/{}/status", sessionId);
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(
                        "Session not found with id: " + sessionId));
        return ResponseEntity.ok(session.getStatus().name());
    }
}

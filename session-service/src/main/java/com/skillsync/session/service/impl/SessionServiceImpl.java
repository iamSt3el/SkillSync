package com.skillsync.session.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skillsync.session.entity.Session;
import com.skillsync.session.entity.SessionStatus;
import com.skillsync.session.repository.SessionRepository;
import com.skillsync.session.service.SessionService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
	
	private final SessionRepository sessionRepository;
	
	private Session getSessionOrThrow(Long id) {
		return sessionRepository.findById(id).orElseThrow(() -> new RuntimeException("Session not found"));
	}
	
	@Override
	public List<Session> getSessionByUser(Long userId) {
		
		return sessionRepository.findByLearnerIdOrMentorId(userId, userId);
	}

	@Override
	public Session bookSession(Session session) {
		session.setStatus(SessionStatus.REQUESTED);
        return sessionRepository.save(session);
	}

	@Override
	public Session acceptSession(Long id) {
		
		Session session = getSessionOrThrow(id);
		
		if(session.getStatus() != SessionStatus.REQUESTED) {
			throw new RuntimeException("Only Requested Session Can be Accepted");
		}
		
		session.setStatus(SessionStatus.ACCEPTED);
		return sessionRepository.save(session);
	}

	@Override
	public Session rejectSession(Long id) {
		Session session = getSessionOrThrow(id);

        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new RuntimeException("Only REQUESTED sessions can be rejected");
        }

        session.setStatus(SessionStatus.REJECTED);
        return sessionRepository.save(session);
	}

	@Override
	public Session cancelSession(Long id) {
		Session session = getSessionOrThrow(id);

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new RuntimeException("Completed sessions cannot be cancelled");
        }

        session.setStatus(SessionStatus.CANCELLED);
        return sessionRepository.save(session);
	}

}

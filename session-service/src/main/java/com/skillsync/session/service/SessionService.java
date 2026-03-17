package com.skillsync.session.service;

import java.util.List;

import com.skillsync.session.entity.Session;

public interface SessionService {
	
	public List<Session> getSessionByUser(Long userId);
	
	public Session bookSession(Session session);
	
	public Session acceptSession(Long id);
	
	public Session rejectSession(Long id);
	
	public Session cancelSession(Long id);
	
}

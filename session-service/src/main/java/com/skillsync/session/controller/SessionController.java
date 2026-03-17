package com.skillsync.session.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsync.session.entity.Session;
import com.skillsync.session.service.SessionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sessions")
public class SessionController {
	
	private final SessionService sessionService;
	
	@PostMapping
	public Session bookSession(@RequestBody Session session) {
		return sessionService.bookSession(session);
	}
	
	@PutMapping("/{id}/accept")
	public Session acceptSession(@PathVariable Long id) {
		
		return sessionService.acceptSession(id);	
	}
	
	@PutMapping("/{id}/reject")
	public Session rejectSession(@PathVariable Long id) {
		
		return sessionService.cancelSession(id);	
	}
	
	@PutMapping("/{id}/cancel")
	public Session cancelSession(@PathVariable Long id) {
		
		return sessionService.cancelSession(id);	
	}
	
	@GetMapping("/user/{userId}")
	public List<Session> getSessionByUser(@PathVariable Long userId) {
		return sessionService.getSessionByUser(userId);
	}
	
}

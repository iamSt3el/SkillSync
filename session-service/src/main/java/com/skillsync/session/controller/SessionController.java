package com.skillsync.session.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsync.session.dto.SessionBookRequest;
import com.skillsync.session.entity.Session;
import com.skillsync.session.service.SessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sessions")
public class SessionController {
	
	private final SessionService sessionService;
	
	@PostMapping
	public ResponseEntity<Session> bookSession(@Valid @RequestBody SessionBookRequest request) {
		return ResponseEntity.ok(sessionService.bookSession(request));
		
	}
	
	@PutMapping("/{id}/accept")
	public ResponseEntity<Session> acceptSession(@PathVariable Long id) {
		
		return ResponseEntity.ok(sessionService.acceptSession(id));	
	}
	
	@PutMapping("/{id}/reject")
	public ResponseEntity<Session> rejectSession(@PathVariable Long id) {
		
		return ResponseEntity.ok(sessionService.rejectSession(id));	
	}
	
	@PutMapping("/{id}/cancel")
	public ResponseEntity<Session> cancelSession(@PathVariable Long id) {
		
		return ResponseEntity.ok(sessionService.cancelSession(id));	
	}
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Session>> getSessionByUser(@PathVariable Long userId) {
		return ResponseEntity.ok(sessionService.getSessionByUser(userId));
	}
	
}

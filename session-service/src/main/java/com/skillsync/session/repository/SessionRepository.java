package com.skillsync.session.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillsync.session.entity.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
	
	List<Session> findByLearnerIdOrMentorId(Long learnerId, Long mentorId);
	
	boolean findByLearnerIdAndMentorId(Long learnerId, Long mentorId);
}

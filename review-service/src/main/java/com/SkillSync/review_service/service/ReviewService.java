package com.SkillSync.review_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.SkillSync.review_service.dto.Mapper;
import com.SkillSync.review_service.dto.ReviewRequestDTO;
import com.SkillSync.review_service.dto.ReviewResponseDTO;
import com.SkillSync.review_service.entity.Review;
import com.SkillSync.review_service.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final Mapper mapper;
	
	public ReviewResponseDTO saveReview(ReviewRequestDTO dto) {
		Review review = mapper.toEntity(dto);
		Review saveReview = reviewRepository.save(review);
		return mapper.toResponseDto(saveReview);
	}
	
	public List<ReviewResponseDTO> getReviewByMentor(Long mentor_id) {
		List<ReviewResponseDTO> reviews = reviewRepository.findByMentorId(mentor_id).stream()
				.map(mapper::toResponseDto)
				.collect(Collectors.toList());
		
		return reviews;	
	}
}

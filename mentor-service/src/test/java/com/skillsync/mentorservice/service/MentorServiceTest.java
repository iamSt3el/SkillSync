package com.skillsync.mentorservice.service;

import com.skillsync.mentorservice.Messaging.MentorAppliedEvent;

import com.skillsync.mentorservice.Messaging.MentorApprovedEvent;
import com.skillsync.mentorservice.Messaging.MentorEventPublisher;
import com.skillsync.mentorservice.dto.request.AvailabilityRequest;
import com.skillsync.mentorservice.dto.request.MentorApplyRequest;
import com.skillsync.mentorservice.dto.response.MentorResponse;
import com.skillsync.mentorservice.dto.response.SkillResponse;
import com.skillsync.mentorservice.entity.Mentor;
import com.skillsync.mentorservice.entity.MentorSkill;
import com.skillsync.mentorservice.enums.MentorStatus;
import com.skillsync.mentorservice.exception.MentorNotFoundException;
import com.skillsync.mentorservice.feign.SkillServiceClient;
import com.skillsync.mentorservice.feign.UserServiceClient;
import com.skillsync.mentorservice.repository.MentorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorServiceTest {

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private SkillServiceClient skillServiceClient;

    @Mock
    private MentorEventPublisher mentorEventPublisher;

    @InjectMocks
    private MentorService mentorService;

    private Mentor mentor;
    private MentorApplyRequest applyRequest;
    private SkillResponse skillResponse1;
    private SkillResponse skillResponse2;

    // This runs before every single test
    @BeforeEach
    void setUp() {
        // Build a sample mentor entity
        mentor = Mentor.builder()
                .id(1L)
                .userId(1L)
                .bio("I am a senior Spring Boot developer with 5 years of experience.")
                .experience(5)
                .hourlyRate(800.0)
                .rating(0.0)
                .reviewCount(0)
                .status(MentorStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .mentorSkills(new ArrayList<>())
                .build();

        // Build a sample apply request
        applyRequest = new MentorApplyRequest();
        applyRequest.setUserId(1L);
        applyRequest.setBio("I am a senior Spring Boot developer with 5 years of experience.");
        applyRequest.setExperience(5);
        applyRequest.setHourlyRate(800.0);
        applyRequest.setSkillIds(Arrays.asList(1L, 2L));

        // Build sample skill responses
        skillResponse1 = SkillResponse.builder()
                .id(1L)
                .name("Spring Boot")
                .category("Backend")
                .build();

        skillResponse2 = SkillResponse.builder()
                .id(2L)
                .name("Java")
                .category("Backend")
                .build();
    }

    // ===================== applyAsMentor tests =====================

    @Test
    void applyAsMentor_Success() {
        // Arrange
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);
        when(skillServiceClient.getSkillbyId(1L)).thenReturn(skillResponse1);
        when(skillServiceClient.getSkillbyId(2L)).thenReturn(skillResponse2);
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentor);
        doNothing().when(mentorEventPublisher).publishMentorApplied(any(MentorAppliedEvent.class));

        // Act
        MentorResponse response = mentorService.applyAsMentor(applyRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getExperience()).isEqualTo(5);
        assertThat(response.getHourlyRate()).isEqualTo(800.0);

        // Verify repository and publisher were called
        verify(mentorRepository, times(1)).existsByUserId(1L);
        verify(mentorRepository, times(1)).save(any(Mentor.class));
        verify(mentorEventPublisher, times(1)).publishMentorApplied(any(MentorAppliedEvent.class));
    }

    @Test
    void applyAsMentor_ThrowsException_WhenUserAlreadyApplied() {
        // Arrange
        when(mentorRepository.existsByUserId(1L)).thenReturn(true);

        // Act and Assert
        assertThatThrownBy(() -> mentorService.applyAsMentor(applyRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User has already applied as a mentor");

        // Verify save was never called
        verify(mentorRepository, never()).save(any(Mentor.class));
        verify(mentorEventPublisher, never()).publishMentorApplied(any(MentorAppliedEvent.class));
    }

    @Test
    void applyAsMentor_WithNoSkills_Success() {
        // Arrange
        applyRequest.setSkillIds(null);
        when(mentorRepository.existsByUserId(1L)).thenReturn(false);
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentor);
        doNothing().when(mentorEventPublisher).publishMentorApplied(any(MentorAppliedEvent.class));

        // Act
        MentorResponse response = mentorService.applyAsMentor(applyRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("PENDING");

        // Verify skill service was never called
        verify(skillServiceClient, never()).getSkillbyId(anyLong());
    }

    // ===================== getMentorById tests =====================

    @Test
    void getMentorById_Success() {
        // Arrange
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));

        // Act
        MentorResponse response = mentorService.getMentorById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getBio()).isEqualTo("I am a senior Spring Boot developer with 5 years of experience.");

        verify(mentorRepository, times(1)).findById(1L);
    }

    @Test
    void getMentorById_ThrowsException_WhenMentorNotFound() {
        // Arrange
        when(mentorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> mentorService.getMentorById(999L))
                .isInstanceOf(MentorNotFoundException.class)
                .hasMessage("Mentor not found with id: 999");

        verify(mentorRepository, times(1)).findById(999L);
    }

    // ===================== getAllActiveMentors tests =====================

    @Test
    void getAllActiveMentors_Success() {
        // Arrange
        mentor.setStatus(MentorStatus.ACTIVE);
        Mentor mentor2 = Mentor.builder()
                .id(2L)
                .userId(2L)
                .bio("I am a machine learning engineer with 4 years of experience.")
                .experience(4)
                .hourlyRate(600.0)
                .rating(0.0)
                .reviewCount(0)
                .status(MentorStatus.ACTIVE)
                .mentorSkills(new ArrayList<>())
                .build();

        when(mentorRepository.findByStatus(MentorStatus.ACTIVE))
                .thenReturn(Arrays.asList(mentor, mentor2));

        // Act
        List<MentorResponse> responses = mentorService.getAllActiveMentors();

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(1).getId()).isEqualTo(2L);

        verify(mentorRepository, times(1)).findByStatus(MentorStatus.ACTIVE);
    }

    @Test
    void getAllActiveMentors_ReturnsEmptyList_WhenNoActiveMentors() {
        // Arrange
        when(mentorRepository.findByStatus(MentorStatus.ACTIVE))
                .thenReturn(new ArrayList<>());

        // Act
        List<MentorResponse> responses = mentorService.getAllActiveMentors();

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();
    }

    // ===================== updateAvailability tests =====================

    @Test
    void updateAvailability_Success() {
        // Arrange
        AvailabilityRequest request = new AvailabilityRequest();
        request.setSchedule("Mon 10:00-12:00, Wed 14:00-16:00");

        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentor);

        // Act
        MentorResponse response = mentorService.updateAvailability(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(mentorRepository, times(1)).findById(1L);
        verify(mentorRepository, times(1)).save(any(Mentor.class));
    }

    @Test
    void updateAvailability_ThrowsException_WhenMentorNotFound() {
        // Arrange
        AvailabilityRequest request = new AvailabilityRequest();
        request.setSchedule("Mon 10:00-12:00");

        when(mentorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> mentorService.updateAvailability(999L, request))
                .isInstanceOf(MentorNotFoundException.class)
                .hasMessage("Mentor not found with id: 999");

        verify(mentorRepository, never()).save(any(Mentor.class));
    }

    // ===================== approveMentor tests =====================

    @Test
    void approveMentor_Success() {
        // Arrange
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentor);
        doNothing().when(mentorEventPublisher).publishMentorApproved(any(MentorApprovedEvent.class));

        // Act
        MentorResponse response = mentorService.approveMentor(1L);

        // Assert
        assertThat(response).isNotNull();
        verify(mentorRepository, times(1)).findById(1L);
        verify(mentorRepository, times(1)).save(any(Mentor.class));
        verify(mentorEventPublisher, times(1)).publishMentorApproved(any(MentorApprovedEvent.class));
    }

    @Test
    void approveMentor_ThrowsException_WhenMentorNotFound() {
        // Arrange
        when(mentorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> mentorService.approveMentor(999L))
                .isInstanceOf(MentorNotFoundException.class)
                .hasMessage("Mentor not found with id: 999");

        verify(mentorRepository, never()).save(any(Mentor.class));
        verify(mentorEventPublisher, never()).publishMentorApproved(any(MentorApprovedEvent.class));
    }

    // ===================== updateRating tests =====================

    @Test
    void updateRating_Success() {
        // Arrange
        mentor.setRating(4.0);
        mentor.setReviewCount(1);
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentor);

        // Act
        mentorService.updateRating(1L, 5.0);

        // Assert
        verify(mentorRepository, times(1)).findById(1L);
        verify(mentorRepository, times(1)).save(any(Mentor.class));
    }

    @Test
    void updateRating_ThrowsException_WhenMentorNotFound() {
        // Arrange
        when(mentorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> mentorService.updateRating(999L, 5.0))
                .isInstanceOf(MentorNotFoundException.class)
                .hasMessage("Mentor not found with id: 999");

        verify(mentorRepository, never()).save(any(Mentor.class));
    }
}
package com.skillsync.skillservice.service;

import com.skillsync.skillservice.dto.request.SkillRequest;
import com.skillsync.skillservice.dto.response.SkillResponse;
import com.skillsync.skillservice.entity.Skill;
import com.skillsync.skillservice.exception.SkillNotFoundException;
import com.skillsync.skillservice.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    private Skill skill1;
    private Skill skill2;
    private Skill skill3;
    private SkillRequest skillRequest;

    // Runs before every single test
    @BeforeEach
    void setUp() {

        skill1 = Skill.builder()
                .id(1L)
                .name("Spring Boot")
                .category("Backend")
                .build();

        skill2 = Skill.builder()
                .id(2L)
                .name("Machine Learning")
                .category("AI/ML")
                .build();

        skill3 = Skill.builder()
                .id(3L)
                .name("Data Structures")
                .category("DSA")
                .build();

        skillRequest = new SkillRequest();
        skillRequest.setName("Spring Boot");
        skillRequest.setCategory("Backend");
    }

    // ===================== createSkill tests =====================

    @Test
    void createSkill_Success() {
        // Arrange
        when(skillRepository.existsByNameIgnoreCase("Spring Boot"))
                .thenReturn(false);
        when(skillRepository.save(any(Skill.class)))
                .thenReturn(skill1);

        // Act
        SkillResponse response = skillService.createSkill(skillRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Spring Boot");
        assertThat(response.getCategory()).isEqualTo("Backend");

        verify(skillRepository, times(1))
                .existsByNameIgnoreCase("Spring Boot");
        verify(skillRepository, times(1))
                .save(any(Skill.class));
    }

    @Test
    void createSkill_ThrowsException_WhenSkillAlreadyExists() {
        // Arrange
        when(skillRepository.existsByNameIgnoreCase("Spring Boot"))
                .thenReturn(true);

        // Act and Assert
        assertThatThrownBy(() -> skillService.createSkill(skillRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Skill already exists: Spring Boot");

        // Verify save was never called
        verify(skillRepository, never()).save(any(Skill.class));
    }

    @Test
    void createSkill_WithDifferentCategory_Success() {
        // Arrange
        SkillRequest newRequest = new SkillRequest();
        newRequest.setName("Machine Learning");
        newRequest.setCategory("AI/ML");

        when(skillRepository.existsByNameIgnoreCase("Machine Learning"))
                .thenReturn(false);
        when(skillRepository.save(any(Skill.class)))
                .thenReturn(skill2);

        // Act
        SkillResponse response = skillService.createSkill(newRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Machine Learning");
        assertThat(response.getCategory()).isEqualTo("AI/ML");

        verify(skillRepository, times(1))
                .existsByNameIgnoreCase("Machine Learning");
        verify(skillRepository, times(1))
                .save(any(Skill.class));
    }

    @Test
    void createSkill_TrimsWhitespace_Success() {
        // Arrange
        SkillRequest requestWithSpaces = new SkillRequest();
        requestWithSpaces.setName("  Spring Boot  ");
        requestWithSpaces.setCategory("  Backend  ");

        when(skillRepository.existsByNameIgnoreCase("  Spring Boot  "))
                .thenReturn(false);
        when(skillRepository.save(any(Skill.class)))
                .thenReturn(skill1);

        // Act
        SkillResponse response = skillService.createSkill(requestWithSpaces);

        // Assert
        assertThat(response).isNotNull();
        verify(skillRepository, times(1))
                .save(any(Skill.class));
    }

    // ===================== getAllSkills tests =====================

    @Test
    void getAllSkills_Success() {
        // Arrange
        when(skillRepository.findAll())
                .thenReturn(Arrays.asList(skill1, skill2, skill3));

        // Act
        List<SkillResponse> responses = skillService.getAllSkills();

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).getName()).isEqualTo("Spring Boot");
        assertThat(responses.get(1).getName()).isEqualTo("Machine Learning");
        assertThat(responses.get(2).getName()).isEqualTo("Data Structures");

        verify(skillRepository, times(1)).findAll();
    }

    @Test
    void getAllSkills_ReturnsTwoSkills_Success() {
        // Arrange
        when(skillRepository.findAll())
                .thenReturn(Arrays.asList(skill1, skill2));

        // Act
        List<SkillResponse> responses = skillService.getAllSkills();

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(1).getId()).isEqualTo(2L);

        verify(skillRepository, times(1)).findAll();
    }

    @Test
    void getAllSkills_ReturnsEmptyList_WhenNoSkillsExist() {
        // Arrange
        when(skillRepository.findAll())
                .thenReturn(new ArrayList<>());

        // Act
        List<SkillResponse> responses = skillService.getAllSkills();

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();

        verify(skillRepository, times(1)).findAll();
    }

    // ===================== getSkillById tests =====================

    @Test
    void getSkillById_Success() {
        // Arrange
        when(skillRepository.findById(1L))
                .thenReturn(Optional.of(skill1));

        // Act
        SkillResponse response = skillService.getSkillById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Spring Boot");
        assertThat(response.getCategory()).isEqualTo("Backend");

        verify(skillRepository, times(1)).findById(1L);
    }

    @Test
    void getSkillById_ReturnsCorrectSkill_ForDifferentId() {
        // Arrange
        when(skillRepository.findById(2L))
                .thenReturn(Optional.of(skill2));

        // Act
        SkillResponse response = skillService.getSkillById(2L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("Machine Learning");
        assertThat(response.getCategory()).isEqualTo("AI/ML");

        verify(skillRepository, times(1)).findById(2L);
    }

    @Test
    void getSkillById_ThrowsException_WhenSkillNotFound() {
        // Arrange
        when(skillRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> skillService.getSkillById(999L))
                .isInstanceOf(SkillNotFoundException.class)
                .hasMessage("Skill not found with id: 999");

        verify(skillRepository, times(1)).findById(999L);
    }

    @Test
    void getSkillById_ThrowsException_WhenIdDoesNotExist() {
        // Arrange
        when(skillRepository.findById(100L))
                .thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> skillService.getSkillById(100L))
                .isInstanceOf(SkillNotFoundException.class)
                .hasMessage("Skill not found with id: 100");

        verify(skillRepository, times(1)).findById(100L);
    }
}
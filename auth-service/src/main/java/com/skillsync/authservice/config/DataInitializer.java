package com.skillsync.authservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.skillsync.authservice.entity.Role;
import com.skillsync.authservice.repository.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!roleRepository.existsByName("ROLE_LEARNER")) {
            Role learnerRole = new Role("ROLE_LEARNER");
            roleRepository.save(learnerRole);
        }
        if (!roleRepository.existsByName("ROLE_MENTOR")) {
            Role mentorRole = new Role("ROLE_MENTOR");
            roleRepository.save(mentorRole);
        }
        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            Role adminRole = new Role("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }
    }
}
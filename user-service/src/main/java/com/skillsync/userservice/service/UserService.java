package com.skillsync.userservice.service;

import com.skillsync.userservice.dto.UserDTO;
import com.skillsync.userservice.entity.User;
import com.skillsync.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(UserDTO::new);
    }

    public UserDTO updateUser(Long id, UserDTO userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
   
            User savedUser = userRepository.save(user);
            return new UserDTO(savedUser);
        }
        return null;
    }

    public UserDTO createUser(User user) {
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }
}
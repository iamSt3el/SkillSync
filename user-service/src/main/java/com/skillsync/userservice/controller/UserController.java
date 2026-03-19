package com.skillsync.userservice.controller;

import com.skillsync.userservice.dto.UserDTO;
import com.skillsync.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authenticated user is accessing their own profile
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Optional<UserDTO> user = userService.getUserById(id);
            if (user.isPresent() && user.get().getEmail().equalsIgnoreCase(email)) {
                return ResponseEntity.ok(user.get());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authenticated user is updating their own profile
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Optional<UserDTO> user = userService.getUserById(id);
            if (user.isPresent() && user.get().getEmail().equalsIgnoreCase(email)) {
                UserDTO updatedUser = userService.updateUser(id, userDetails);
                return ResponseEntity.ok(updatedUser);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

//    @PostMapping
//    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {
//        UserDTO createdUser = userService.createUser(user);
//        return ResponseEntity.ok(createdUser);
//    }
}
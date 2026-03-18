package com.skillsync.userservice.controller.internal;

import com.skillsync.userservice.dto.UserDTO;
import com.skillsync.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}/email")
    public ResponseEntity<Map<String, String>> getUserEmail(@PathVariable Long id) {
        Optional<UserDTO> user = userService.getUserById(id);
        return user.map(u -> ResponseEntity.ok(Map.of("email", u.getEmail())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

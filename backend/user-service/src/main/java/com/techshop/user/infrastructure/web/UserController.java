package com.techshop.user.infrastructure.web;

import com.techshop.user.application.service.UserService;
import com.techshop.user.infrastructure.web.dto.UserRequest;
import com.techshop.user.infrastructure.web.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUserProfile(@Valid @RequestBody UserRequest request) {
        return new ResponseEntity<>(userService.createUserProfile(request), HttpStatus.CREATED);
    }

    @GetMapping("/{keycloakUserId}")
    public ResponseEntity<UserResponse> getUserByKeycloakId(@PathVariable String keycloakUserId) {
        return ResponseEntity.ok(userService.getUserByKeycloakId(keycloakUserId));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<UserResponse>> getUsersByTenant(@PathVariable String tenantId) {
        return ResponseEntity.ok(userService.getUsersByTenant(tenantId));
    }
}
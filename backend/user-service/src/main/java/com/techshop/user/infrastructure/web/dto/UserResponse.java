package com.techshop.user.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String keycloakUserId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String tenantId;
    private Instant createdAt;
}
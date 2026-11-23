package com.portfolio.ecommerce.dto;

import com.portfolio.ecommerce.entity.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        Role role,
        LocalDateTime createdAt
) {
}

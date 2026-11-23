package com.portfolio.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 80, message = "Name must be 80 characters or fewer")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email")
        @Size(max = 120, message = "Email must be 120 characters or fewer")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters")
        String password,

        @Size(max = 20, message = "Phone must be 20 characters or fewer")
        String phone,

        @Size(max = 255, message = "Address must be 255 characters or fewer")
        String address
) {
}

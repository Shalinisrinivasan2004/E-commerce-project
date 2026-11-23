package com.portfolio.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must be 120 characters or fewer")
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description must be 1000 characters or fewer")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
        @Digits(integer = 8, fraction = 2, message = "Price can have at most 8 digits and 2 decimal places")
        BigDecimal price,

        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock cannot be negative")
        @Max(value = 100000, message = "Stock cannot exceed 100000")
        Integer stockQuantity,

        @NotBlank(message = "Category is required")
        @Size(max = 60, message = "Category must be 60 characters or fewer")
        String category,

        @Size(max = 500, message = "Image URL must be 500 characters or fewer")
        String imageUrl
) {
}

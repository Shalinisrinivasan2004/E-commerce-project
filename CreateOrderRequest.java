package com.portfolio.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotBlank(message = "Shipping address is required")
        @Size(max = 255, message = "Shipping address must be 255 characters or fewer")
        String shippingAddress
) {
}

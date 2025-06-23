package org.example.shopyapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemDto(
    @NotNull(message = "Product ID is required")
    Long id,
    
    @NotNull(message = "Product name is required")
    String productName,
    
    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity
) { }

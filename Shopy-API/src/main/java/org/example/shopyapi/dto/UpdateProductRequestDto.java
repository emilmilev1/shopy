package org.example.shopyapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.example.shopyapi.model.Point;

public record UpdateProductRequestDto(
    @Min(value = 0, message = "Quantity must be non-negative")
    int newQuantity,
    
    @Min(value = 0, message = "Price must be non-negative")
    double newPrice,
    
    @NotNull(message = "Location is required")
    Point newLocation
) { }

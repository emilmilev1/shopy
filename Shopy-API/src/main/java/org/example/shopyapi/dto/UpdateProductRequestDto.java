package org.example.shopyapi.dto;

import org.example.shopyapi.model.Point;

public record UpdateProductRequestDto(
        int newQuantity,
        double newPrice,
        Point newLocation
) { }

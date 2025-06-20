package org.example.shopyapi.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlaceOrderRequestDto(
        @NotNull(message = "The items list cannot be missing or null.")
        List<OrderItemDto> items) {
}

package org.example.shopyapi.dto;

import org.example.shopyapi.model.Order;
import org.example.shopyapi.model.OrderStatus;

public record OrderResponseDto(Long id, OrderStatus status, String message) {
    public static OrderResponseDto fromEntity(Order order, String message) {
        return new OrderResponseDto(order.getId(), order.getStatus(), message);
    }
}

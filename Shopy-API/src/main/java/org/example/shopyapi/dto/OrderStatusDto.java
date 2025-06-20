package org.example.shopyapi.dto;

import org.example.shopyapi.model.Order;
import org.example.shopyapi.model.OrderStatus;

public record OrderStatusDto(Long id, OrderStatus status) {
    public static OrderStatusDto fromEntity(Order order) {
        return new OrderStatusDto(order.getId(), order.getStatus());
    }
}

package org.example.shopyapi.dto;

import org.example.shopyapi.model.OrderStatus;

public record OrderStatusDto(Long id, OrderStatus status) {
}

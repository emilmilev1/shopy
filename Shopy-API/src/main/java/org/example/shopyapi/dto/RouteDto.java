package org.example.shopyapi.dto;

import org.example.shopyapi.model.Order;
import org.example.shopyapi.model.OrderStatus;

import java.util.List;
import java.util.stream.Collectors;

public record RouteDto(Long orderId, OrderStatus status, List<List<Integer>> visitedLocations) {
    public static RouteDto fromEntity(Order order) {
        List<List<Integer>> locations = order.getRoute().stream()
                .map(point -> List.of(point.getX(), point.getY()))
                .collect(Collectors.toList());

        return new RouteDto(order.getId(), order.getStatus(), locations);
    }
}

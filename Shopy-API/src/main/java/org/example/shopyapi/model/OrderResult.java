package org.example.shopyapi.model;

import java.util.List;

public record OrderResult(
        Long orderId,
        OrderStatus status,
        String message,
        List<Point> route
) { }

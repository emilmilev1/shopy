package org.example.shopyapi.controller;

import org.example.shopyapi.dto.RouteDto;
import org.example.shopyapi.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    private final OrderService orderService;

    public RouteController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<RouteDto> getRouteForOrder(@RequestParam Long orderId) {
        return orderService.findById(orderId)
                .map(RouteDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

package org.example.shopyapi.controller;

import jakarta.validation.Valid;
import org.example.shopyapi.dto.OrderStatusDto;
import org.example.shopyapi.dto.PlaceOrderRequestDto;
import org.example.shopyapi.model.OrderResult;
import org.example.shopyapi.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Object> placeOrder(@Valid @RequestBody PlaceOrderRequestDto requestDto) {
        OrderResult result = orderService.processOrder(requestDto);
        return ResponseEntity.ok(new java.util.LinkedHashMap<>() {{
            put("id", result.orderId());
            put("status", result.status());
            put("message", result.message());
        }});
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderStatusDto> getOrderStatus(@PathVariable Long id) {
        return orderService.findById(id)
                .map(order -> new OrderStatusDto(order.getId(), order.getStatus()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Collection<OrderStatusDto>> listOrders() {
        var orders = orderService.getAllProducts().stream()
                .map(OrderStatusDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }
}

package org.example.shopyapi.controller;

import jakarta.validation.Valid;
import org.example.shopyapi.dto.OrderStatusDto;
import org.example.shopyapi.dto.PlaceOrderRequestDto;
import org.example.shopyapi.model.OrderResult;
import org.example.shopyapi.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        OrderResult result = orderService.processOrder(requestDto, userEmail);
        return ResponseEntity.ok(new java.util.LinkedHashMap<>() {{
            put("id", result.orderId());
            put("status", result.status());
            put("message", result.message());
        }});
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderStatusDto> getOrderStatus(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        return orderService.findByIdAndUser(id, userEmail)
                .map(order -> new OrderStatusDto(order.getId(), order.getStatus()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Collection<OrderStatusDto>> listOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        var orders = orderService.getOrdersByUser(userEmail).stream()
                .map(OrderStatusDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }
}

package org.example.shopyapi.controller;

import jakarta.validation.Valid;
import org.example.shopyapi.dto.OrderStatusDto;
import org.example.shopyapi.dto.PlaceOrderRequestDto;
import org.example.shopyapi.model.OrderResult;
import org.example.shopyapi.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResult> placeOrder(@Valid @RequestBody PlaceOrderRequestDto requestDto) {
        OrderResult result = orderService.processOrder(requestDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderStatusDto> getOrderStatus(@PathVariable Long id) {
        return orderService.findById(id)
                .map(order -> new OrderStatusDto(order.getId(), order.getStatus()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

package org.example.shopyapi.controller;

import jakarta.validation.Valid;
import org.example.shopyapi.dto.PlaceOrderRequestDto;
import org.example.shopyapi.model.OrderResult;
import org.example.shopyapi.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

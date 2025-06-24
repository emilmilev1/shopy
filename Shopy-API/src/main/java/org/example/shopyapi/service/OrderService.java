package org.example.shopyapi.service;

import org.example.shopyapi.dto.OrderItemDto;
import org.example.shopyapi.dto.PlaceOrderRequestDto;
import org.example.shopyapi.model.*;
import org.example.shopyapi.repository.OrderRepository;
import org.example.shopyapi.repository.OrderItemRepository;
import org.example.shopyapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryService inventoryService;
    private final RoutingService routingService;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, InventoryService inventoryService, RoutingService routingService, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryService = inventoryService;
        this.routingService = routingService;
        this.userRepository = userRepository;
    }

    public OrderResult processOrder(PlaceOrderRequestDto requestDto, String userEmail) {
        // Find the user
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + userEmail);
        }
        User user = userOpt.get();

        List<String> missingItems = checkStock(requestDto);

        List<OrderItem> orderItems = requestDto.items().stream()
                .map(item -> new OrderItem(item.productName(), item.quantity()))
                .collect(Collectors.toList());

        Order order;
        String message;

        if (!missingItems.isEmpty()) {
            message = "Not enough stock to fulfill your order.";
            order = new Order();
            order.setStatus(OrderStatus.FAIL);
            order.setUser(user);
            order.addItems(orderItems);
            order.setRoute(List.of());
            order = orderRepository.save(order);
            orderItemRepository.saveAll(orderItems);
            return new OrderResult(order.getId(), order.getStatus(), message, order.getRoute());
        }

        fulfillOrder(requestDto);

        List<Point> locationsToVisit = getLocationsForOrder(requestDto);
        List<Point> route = routingService.calculateOptimalRoute(locationsToVisit);
        message = "Your order is ready! Please collect it.";

        order = new Order();
        order.setStatus(OrderStatus.SUCCESS);
        order.setUser(user);
        order.addItems(orderItems);
        order.setRoute(route);
        order = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        return new OrderResult(order.getId(), order.getStatus(), message, order.getRoute());
    }

    private List<String> checkStock(PlaceOrderRequestDto requestDto) {
        List<String> missingItems = new ArrayList<>();
        for (OrderItemDto item : requestDto.items()) {
            Optional<Product> productOpt = inventoryService.findByName(item.productName());

            if (productOpt.isEmpty()) {
                missingItems.add(item.productName() + ": product not found");
            } else {
                Product product = productOpt.get();
                if (product.getQuantity() < item.quantity()) {
                    missingItems.add(product.getName() + " (requested " + item.quantity() + ", available " + product.getQuantity() + ")");
                }
            }
        }
        return missingItems;
    }

    private void fulfillOrder(PlaceOrderRequestDto requestDto) {
        for (OrderItemDto item : requestDto.items()) {
            inventoryService.findByName(item.productName())
                    .ifPresent(p -> inventoryService.reduceStock(p.getId(), item.quantity()));
        }
    }

    private List<Point> getLocationsForOrder(PlaceOrderRequestDto requestDto) {
        return requestDto.items().stream()
                .map(item -> inventoryService.findByName(item.productName()))
                .flatMap(Optional::stream)
                .map(Product::getLocation)
                .distinct()
                .collect(Collectors.toList());
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> findByIdAndUser(Long id, String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        return orderRepository.findById(id)
                .filter(order -> order.getUser() != null && order.getUser().getId().equals(userOpt.get().getId()));
    }

    public Collection<Order> getAllProducts() {
        return orderRepository.findAll();
    }

    public Collection<Order> getOrdersByUser(String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        return orderRepository.findByUser(userOpt.get());
    }
}

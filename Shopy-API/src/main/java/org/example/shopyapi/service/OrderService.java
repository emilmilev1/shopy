package org.example.shopyapi.service;

import org.example.shopyapi.dto.OrderItemDto;
import org.example.shopyapi.dto.PlaceOrderRequestDto;
import org.example.shopyapi.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final InventoryService inventoryService;
    private final RoutingService routingService;

    public OrderService(InventoryService inventoryService, RoutingService routingService) {
        this.inventoryService = inventoryService;
        this.routingService = routingService;
    }

    public OrderResult processOrder(PlaceOrderRequestDto requestDto) {
        List<String> missingItems = checkStock(requestDto);

        if (!missingItems.isEmpty()) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("❌ We cannot fulfill your order right now – not enough stock\n");
            messageBuilder.append("\tMissing items:\n");
            for (String item : missingItems) {
                messageBuilder.append("\t\t").append(item).append("\n");
            }

            return new OrderResult(OrderStatus.FAIL, messageBuilder.toString().trim(), null);
        }

        fulfillOrder(requestDto);

        List<Point> locationsToVisit = getLocationsForOrder(requestDto);
        List<Point> route = routingService.calculateOptimalRoute(locationsToVisit);

        String message = "✅ Order ready! Please collect it at the desk.";
        return new OrderResult(OrderStatus.SUCCESS, message, route);
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
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Product::getLocation)
                .distinct()
                .collect(Collectors.toList());
    }
}

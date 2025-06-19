package org.example.service;

import org.example.interfaces.IOrderService;
import org.example.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderService implements IOrderService {
    private final InventoryService inventoryService;
    private final RoutingService routingService;

    public OrderService(InventoryService inventoryService, RoutingService routingService) {
        this.inventoryService = inventoryService;
        this.routingService = routingService;
    }

    @Override
    public OrderResult processOrder(Order order) {
        List<String> missingItems = checkStock(order);

        if (!missingItems.isEmpty()) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("❌ We cannot fulfill your order right now – not enough stock\n");
            messageBuilder.append("\tMissing items:\n");
            for (String item : missingItems) {
                messageBuilder.append("\t\t").append(item).append("\n");
            }

            return new OrderResult(OrderStatus.FAIL, messageBuilder.toString().trim(), null);
        }

        fulfillOrder(order);

        List<Point> locationsToVisit = getLocationsForOrder(order);
        List<Point> route = routingService.calculateOptimalRoute(locationsToVisit);

        String message = "✅ Order ready! Please collect it at the desk.";
        return new OrderResult(OrderStatus.SUCCESS, message, route);
    }

    private List<String> checkStock(Order order) {
        List<String> missingItems = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : order.getRequestedProducts().entrySet()) {
            String productName = entry.getKey();
            int requestedQuantity = entry.getValue();
            Optional<Product> productOpt = inventoryService.getProduct(productName);

            if (productOpt.isEmpty()) {
                missingItems.add(productName + ": product not found");
            } else {
                Product product = productOpt.get();
                if (product.getQuantity() < requestedQuantity) {
                    missingItems.add(product.getName() + " (requested " + requestedQuantity + ", available " + product.getQuantity() + ")");
                }
            }
        }
        return missingItems;
    }

    private void fulfillOrder(Order order) {
        for (Map.Entry<String, Integer> entry : order.getRequestedProducts().entrySet()) {
            inventoryService.reduceStock(entry.getKey(), entry.getValue());
        }
    }

    private List<Point> getLocationsForOrder(Order order) {
        return order.getRequestedProducts().keySet().stream()
                .map(inventoryService::getProduct)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Product::getLocation)
                .distinct()
                .collect(Collectors.toList());
    }
}

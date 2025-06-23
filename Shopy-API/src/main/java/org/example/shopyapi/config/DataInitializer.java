package org.example.shopyapi.config;

import org.example.shopyapi.model.*;
import org.example.shopyapi.repository.OrderRepository;
import org.example.shopyapi.repository.ProductRepository;
import org.example.shopyapi.service.InventoryService;
import org.example.shopyapi.service.OrderService;
import org.example.shopyapi.service.RoutingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final InventoryService inventoryService;

    private final OrderService orderService;

    @Value("${app.initialize.data:false}")
    private boolean shouldInitializeData;

    public DataInitializer(ProductRepository productRepository, OrderRepository orderRepository, InventoryService inventoryService, OrderService orderService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (shouldInitializeData) {
            initializeData();
        }
    }

    public void initializeData() {
        if (productRepository.count() == 0) {
            initializeProducts();
            initializeOrders();
            System.out.println("✅ Database initialized with sample data!");
        } else {
            System.out.println("ℹ️ Database already contains data, skip init.");
        }
    }

    private void initializeProducts() {
        List<Product> products = Arrays.asList(
            new Product("Coca-Cola", 1.50, 100, new Point(0, 1)),
            new Product("Pepsi", 1.45, 80, new Point(1, 0)),
            new Product("Apples", 0.50, 200, new Point(1, 1)),
            new Product("Bananas", 0.30, 150, new Point(2, 0)),
            new Product("Bread", 2.00, 50, new Point(2, 1)),
            new Product("Milk", 1.20, 75, new Point(0, 2)),
            new Product("Cheese", 3.50, 40, new Point(1, 2)),
            new Product("Chicken", 8.00, 30, new Point(2, 2)),
            new Product("Rice", 1.80, 60, new Point(3, 0)),
            new Product("Pasta", 1.20, 90, new Point(3, 1)),
            new Product("Tomatoes", 0.80, 120, new Point(0, 3)),
            new Product("Potatoes", 0.60, 100, new Point(1, 3)),
            new Product("Onions", 0.40, 80, new Point(2, 3)),
            new Product("Carrots", 0.70, 70, new Point(3, 2)),
            new Product("Lettuce", 1.00, 45, new Point(3, 3))
        );

        productRepository.saveAll(products);
        System.out.println("📦 Created " + products.size() + " products");
    }

    private void initializeOrders() {
        createSampleOrder("Order 1 - Small", Arrays.asList(
            new OrderItem("Coca-Cola", 2),
            new OrderItem("Apples", 5)
        ));

        createSampleOrder("Order 2 - Medium", Arrays.asList(
            new OrderItem("Bread", 1),
            new OrderItem("Milk", 2),
            new OrderItem("Cheese", 1),
            new OrderItem("Bananas", 3)
        ));

        createSampleOrder("Order 3 - Large", Arrays.asList(
            new OrderItem("Chicken", 2),
            new OrderItem("Rice", 3),
            new OrderItem("Tomatoes", 4),
            new OrderItem("Onions", 2),
            new OrderItem("Carrots", 1)
        ));

        createSampleOrder("Order 4 - Beverages", Arrays.asList(
            new OrderItem("Coca-Cola", 5),
            new OrderItem("Pepsi", 3),
            new OrderItem("Milk", 1)
        ));

        createSampleOrder("Order 5 - Fruits", Arrays.asList(
            new OrderItem("Apples", 10),
            new OrderItem("Bananas", 8),
            new OrderItem("Tomatoes", 6)
        ));

        createSampleOrder("Order 6 - Kitchen Essentials", Arrays.asList(
            new OrderItem("Rice", 2),
            new OrderItem("Pasta", 3),
            new OrderItem("Onions", 4),
            new OrderItem("Potatoes", 5)
        ));

        createFailedOrder("Order 7 - Failed (Out of Stock)", Arrays.asList(
            new OrderItem("Coca-Cola", 999),
            new OrderItem("Chicken", 50)
        ));

        System.out.println("📋 Created sample orders");
    }

    private void createSampleOrder(String description, List<OrderItem> items) {
        try {
            Order order = new Order();
            order.setStatus(OrderStatus.SUCCESS);
            order.addItems(items);
            
            List<Point> locationsToVisit = items.stream()
                .map(item -> inventoryService.findByName(item.getProductName()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(Product::getLocation)
                .distinct()
                .toList();
            
            RoutingService routingService = new RoutingService();
            List<Point> route = routingService.calculateOptimalRoute(locationsToVisit);
            order.setRoute(route);
            
            orderRepository.save(order);
            
            items.forEach(item -> {
                inventoryService.findByName(item.getProductName())
                    .ifPresent(product -> inventoryService.reduceStock(product.getId(), item.getQuantity()));
            });
            
            System.out.println("✅ " + description + " - ID: " + order.getId());
            
        } catch (Exception e) {
            System.out.println("❌ Failed to create " + description + ": " + e.getMessage());
        }
    }

    private void createFailedOrder(String description, List<OrderItem> items) {
        try {
            Order order = new Order();
            order.setStatus(OrderStatus.FAIL);
            order.addItems(items);
            order.setRoute(List.of());
            
            orderRepository.save(order);
            System.out.println("❌ " + description + " - ID: " + order.getId());
            
        } catch (Exception e) {
            System.out.println("❌ Failed to create " + description + ": " + e.getMessage());
        }
    }
} 
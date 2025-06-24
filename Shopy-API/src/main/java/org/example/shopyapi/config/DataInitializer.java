package org.example.shopyapi.config;

import org.example.shopyapi.model.*;
import org.example.shopyapi.repository.OrderRepository;
import org.example.shopyapi.repository.ProductRepository;
import org.example.shopyapi.repository.UserRepository;
import org.example.shopyapi.service.InventoryService;
import org.example.shopyapi.service.OrderService;
import org.example.shopyapi.service.RoutingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.initialize.data:false}")
    private boolean shouldInitializeData;

    public DataInitializer(ProductRepository productRepository, OrderRepository orderRepository, InventoryService inventoryService, OrderService orderService, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (shouldInitializeData) {
            initializeData();
        }
    }

    public void initializeData() {
        if (productRepository.count() == 0) {
            initializeUsersWithProductsAndOrders();
            System.out.println("✅ Database initialized with user-specific data!");
        } else {
            System.out.println("ℹ️ Database already contains data, skip init.");
        }
    }

    private void initializeUsersWithProductsAndOrders() {
        String password = passwordEncoder.encode("password123");
        List<User> users = Arrays.asList(
            createUser("Alice Smith", "alice@example.com", "1234567890", "123 Main St", password),
            createUser("Bob Johnson", "bob@example.com", "2345678901", "456 Oak Ave", password),
            createUser("Carol Lee", "carol@example.com", "3456789012", "789 Pine Rd", password),
            createUser("David Kim", "david@example.com", "4567890123", "321 Maple Dr", password),
            createUser("Eva Brown", "eva@example.com", "5678901234", "654 Cedar Ln", password)
        );
        userRepository.saveAll(users);
        System.out.println("👤 Created 5 users");

        // Create user-specific products and orders
        createUserProductsAndOrders(users.get(0), "Alice", Arrays.asList(
            new Product("Coca-Cola", 1.50, 100, new Point(0, 1)),
            new Product("Apples", 0.50, 200, new Point(1, 1)),
            new Product("Bread", 2.00, 50, new Point(2, 1)),
            new Product("Milk", 1.20, 75, new Point(0, 2))
        ));

        createUserProductsAndOrders(users.get(1), "Bob", Arrays.asList(
            new Product("Pepsi", 1.45, 80, new Point(1, 0)),
            new Product("Cheese", 3.50, 40, new Point(1, 2)),
            new Product("Chicken", 8.00, 30, new Point(2, 2)),
            new Product("Rice", 1.80, 60, new Point(3, 0))
        ));

        createUserProductsAndOrders(users.get(2), "Carol", Arrays.asList(
            new Product("Bananas", 0.30, 150, new Point(2, 0)),
            new Product("Pasta", 1.20, 90, new Point(3, 1)),
            new Product("Tomatoes", 0.80, 120, new Point(0, 3))
        ));

        createUserProductsAndOrders(users.get(3), "David", Arrays.asList(
            new Product("Potatoes", 0.60, 100, new Point(1, 3)),
            new Product("Onions", 0.40, 80, new Point(2, 3)),
            new Product("Carrots", 0.70, 70, new Point(3, 2))
        ));

        createUserProductsAndOrders(users.get(4), "Eva", Arrays.asList(
            new Product("Lettuce", 1.00, 45, new Point(3, 3)),
            new Product("Orange Juice", 2.50, 60, new Point(0, 0)),
            new Product("Yogurt", 1.80, 85, new Point(1, 1))
        ));
    }

    private User createUser(String name, String email, String telephone, String address, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setTelephone(telephone);
        user.setAddress(address);
        user.setPassword(password);
        return user;
    }

    private void createUserProductsAndOrders(User user, String userName, List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            product.setId((long) (i + 1));
            product.setUser(user);
            productRepository.save(product);
        }
        System.out.println("📦 Created " + products.size() + " products for " + userName + " with IDs 1-" + products.size());

        if (products.isEmpty()) {
            System.out.println("⚠️ No products for " + userName + ", skipping order creation");
            return;
        }

        createUserOrder(user, userName + "'s Order 1", Arrays.asList(
            new OrderItem(products.get(0).getName(), 2),
            new OrderItem(products.get(1).getName(), 3)
        ));

        if (products.size() > 2) {
            createUserOrder(user, userName + "'s Order 2", Arrays.asList(
                new OrderItem(products.get(2).getName(), 1),
                new OrderItem(products.get(0).getName(), 1)
            ));
        }

        createFailedUserOrder(user, userName + "'s Failed Order", Arrays.asList(
            new OrderItem(products.get(0).getName(), 999),
            new OrderItem("Non-existent Product", 50)
        ));
    }

    private void createUserOrder(User user, String description, List<OrderItem> items) {
        try {
            for (OrderItem item : items) {
                if (!inventoryService.findByNameAndUser(item.getProductName(), user).isPresent()) {
                    System.out.println("⚠️ Product '" + item.getProductName() + "' not found for user " + user.getEmail() + ", skipping order creation");
                    return;
                }
            }

            Order order = new Order();
            order.setStatus(OrderStatus.SUCCESS);
            order.setUser(user);
            order.addItems(items);
            
            List<Point> locationsToVisit = items.stream()
                .map(item -> inventoryService.findByNameAndUser(item.getProductName(), user))
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
                inventoryService.findByNameAndUser(item.getProductName(), user)
                    .ifPresent(product -> inventoryService.reduceStock(product.getId(), item.getQuantity()));
            });
            
            System.out.println("✅ " + description + " for " + user.getEmail() + " - Order ID: " + order.getId());
            
        } catch (Exception e) {
            System.out.println("❌ Failed to create " + description + " for " + user.getEmail() + ": " + e.getMessage());
        }
    }

    private void createFailedUserOrder(User user, String description, List<OrderItem> items) {
        try {
            Order order = new Order();
            order.setStatus(OrderStatus.FAIL);
            order.setUser(user);
            order.addItems(items);
            order.setRoute(List.of());
            
            orderRepository.save(order);
            System.out.println("❌ " + description + " for " + user.getEmail() + " - Order ID: " + order.getId());
            
        } catch (Exception e) {
            System.out.println("❌ Failed to create " + description + " for " + user.getEmail() + ": " + e.getMessage());
        }
    }
} 
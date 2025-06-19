package org.example;

import org.example.model.Order;
import org.example.model.OrderResult;
import org.example.model.Point;
import org.example.model.Product;
import org.example.service.InventoryService;
import org.example.service.OrderService;
import org.example.service.RoutingService;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {
    private static final InventoryService inventoryService = new InventoryService();
    private static final RoutingService routingService = new RoutingService();
    private static final OrderService orderService = new OrderService(inventoryService, routingService);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to Mini Shopy!");

        while (true) {
            printMainMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> createProduct();
                case "2" -> listProducts();
                case "3" -> updateProduct();
                case "4" -> deleteProduct();
                case "5" -> placeNewOrder();
                case "6" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please enter a number from 1 to 6.");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Create product");
        System.out.println("2. List products");
        System.out.println("3. Update product");
        System.out.println("4. Delete product");
        System.out.println("5. Place new order");
        System.out.println("6. Exit");
        System.out.print("> ");
    }

    private static void createProduct() {
        try {
            System.out.print("\nEnter product name: ");
            String name = scanner.nextLine();
            System.out.print("Enter quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter price: ");
            double price = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter warehouse location X: ");
            int x = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter warehouse location Y: ");
            int y = Integer.parseInt(scanner.nextLine());

            inventoryService.createProduct(new Product(name, price, quantity, new Point(x, y)));
            System.out.println("Product created successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please ensure numbers are entered correctly.");
        }
    }

    private static void listProducts() {
        System.out.println("\nAvailable Products:");
        var products = inventoryService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products in inventory.");
            return;
        }
        AtomicInteger counter = new AtomicInteger(1);
        products.forEach(product -> System.out.println(counter.getAndIncrement() + ". " + product.toString()));
    }

    private static void updateProduct() {
        try {
            System.out.print("Enter the name of the product to update: ");
            String name = scanner.nextLine();
            if (inventoryService.getProduct(name).isEmpty()) {
                System.out.println("Product not found.");
                return;
            }
            System.out.print("Enter new quantity: ");
            int newQuantity = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter new price: ");
            double newPrice = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter new warehouse location X: ");
            int newX = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter new warehouse location Y: ");
            int newY = Integer.parseInt(scanner.nextLine());

            inventoryService.updateProduct(name, newQuantity, newPrice, new Point(newX, newY));
            System.out.println("Product updated successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please ensure numbers are entered correctly.");
        }
    }

    private static void deleteProduct() {
        System.out.print("Enter the name of the product to delete: ");
        String name = scanner.nextLine();
        if (inventoryService.deleteProduct(name)) {
            System.out.println("Product deleted successfully.");
        } else {
            System.out.println("Product not found.");
        }
    }

    private static void placeNewOrder() {
        System.out.println("\nPlacing a new order...");
        Map<String, Integer> requestedProducts = new HashMap<>();

        while (true) {
            System.out.print("Enter product name (or 'done' to finish): ");
            String productName = scanner.nextLine();
            if (productName.equalsIgnoreCase("done")) {
                break;
            }

            if (inventoryService.getProduct(productName).isEmpty()) {
                System.out.println("Product not found in inventory. Please try again.");
                continue;
            }

            try {
                System.out.print("Enter quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine());
                if (quantity > 0) {
                    requestedProducts.put(productName, quantity);
                } else {
                    System.out.println("Quantity must be greater than zero.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity. Please enter a valid number.");
            }
        }

        if (requestedProducts.isEmpty()) {
            System.out.println("Order cancelled as no products were added.");
            return;
        }

        System.out.println("\nProcessing your order...");
        Order order = new Order(requestedProducts);
        OrderResult result = orderService.processOrder(order);

        System.out.println("Order status: " + result.status());
        if (result.route() != null) {
            String routeStr = result.route().stream()
                    .map(p -> "[" + p.x() + "," + p.y() + "]")
                    .collect(Collectors.joining(", "));
            System.out.println("Visited locations: " + routeStr);
        }
        System.out.println("Message: " + result.message());
    }
}
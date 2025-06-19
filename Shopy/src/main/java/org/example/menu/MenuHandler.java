package org.example.menu;

import org.example.interfaces.IMenuHandler;
import org.example.model.*;
import org.example.service.InventoryService;
import org.example.service.OrderService;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MenuHandler implements IMenuHandler {
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final Scanner scanner;

    public MenuHandler(InventoryService inventoryService, OrderService orderService, Scanner scanner) {
        this.inventoryService = inventoryService;
        this.orderService = orderService;
        this.scanner = scanner;
    }

    @Override
    public void createProduct() {
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

    @Override
    public void listProducts() {
        System.out.println("\nAvailable Products:");
        var products = inventoryService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products in inventory.");
            return;
        }
        AtomicInteger counter = new AtomicInteger(1);
        products.forEach(product -> System.out.println(counter.getAndIncrement() + ". " + product.toString()));
    }

    @Override
    public void updateProduct() {
        try {
            System.out.print("\nEnter the name of the product to update: ");
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

    @Override
    public void deleteProduct() {
        System.out.print("\nEnter the name of the product to delete: ");
        String name = scanner.nextLine();
        if (inventoryService.deleteProduct(name)) {
            System.out.println("Product deleted successfully.");
        } else {
            System.out.println("Product not found.");
        }
    }

    @Override
    public void placeNewOrder() {
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
        OrderResult result = orderService.processOrder(new Order(requestedProducts));

        System.out.println("Order status: " + result.status());
        if (result.status() == OrderStatus.SUCCESS) {
            String routeStr = result.route().stream()
                    .map(p -> "[" + p.x() + "," + p.y() + "]")
                    .collect(Collectors.joining(", "));
            System.out.println("Visited locations: " + routeStr);
        }
        System.out.println("Message: " + result.message());
    }
}

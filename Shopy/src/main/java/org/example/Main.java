package org.example;

import org.example.command.*;
import org.example.interfaces.Command;
import org.example.menu.MenuHandler;
import org.example.registry.CommandRegistry;
import org.example.service.InventoryService;
import org.example.service.OrderService;
import org.example.service.RoutingService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final InventoryService inventoryService = new InventoryService();
        final RoutingService routingService = new RoutingService();
        final OrderService orderService = new OrderService(inventoryService, routingService);

        final Scanner scanner = new Scanner(System.in);

        final MenuHandler menuHandler = new MenuHandler(inventoryService, orderService, scanner);
        CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.register("1", new CreateProductCommand(menuHandler));
        commandRegistry.register("2", new ListProductsCommand(menuHandler));
        commandRegistry.register("3", new UpdateProductCommand(menuHandler));
        commandRegistry.register("4", new DeleteProductCommand(menuHandler));
        commandRegistry.register("5", new PlaceOrderCommand(menuHandler));

        System.out.println("Welcome to Mini Shopy!");

        while (true) {
            printMainMenu();
            String choice = scanner.nextLine();
            Command command = commandRegistry.getCommand(choice);

            if (command != null) {
                command.execute();
            } else if (choice.equals("6")) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid option. Please enter a number from 1 to 6.");
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
}
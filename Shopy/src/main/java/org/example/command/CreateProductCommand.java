package org.example.command;

import org.example.interfaces.Command;
import org.example.menu.MenuHandler;

public class CreateProductCommand implements Command {
    private final MenuHandler menuHandler;

    public CreateProductCommand(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
    }

    @Override
    public void execute() {
        menuHandler.createProduct();
    }
}
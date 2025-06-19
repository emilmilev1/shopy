package org.example.command;

import org.example.interfaces.Command;
import org.example.menu.MenuHandler;

public class DeleteProductCommand implements Command {
    private final MenuHandler menuHandler;

    public DeleteProductCommand(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
    }

    @Override
    public void execute() {
        menuHandler.deleteProduct();
    }
}

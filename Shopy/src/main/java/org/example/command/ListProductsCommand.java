package org.example.command;

import org.example.interfaces.Command;
import org.example.menu.MenuHandler;

public class ListProductsCommand implements Command {
    private final MenuHandler menuHandler;

    public ListProductsCommand(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
    }

    @Override
    public void execute() {
        menuHandler.listProducts();
    }
}

package org.example.command;

import org.example.interfaces.Command;
import org.example.menu.MenuHandler;

public class PlaceOrderCommand implements Command {
    private final MenuHandler menuHandler;

    public PlaceOrderCommand(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
    }

    @Override
    public void execute() {
        menuHandler.placeNewOrder();
    }
}

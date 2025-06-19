package org.example.model;

import java.util.Locale;

public class Product {
    private String name;
    private double price;
    private int quantity;
    private Point location;

    public Product(String name, double price, int quantity, Point location) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.location = location;
    }

    public String getName() {
        return this.name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return this.price;
    }

    private void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return this.quantity;
    }

    private void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Point getLocation() {
        return this.location;
    }

    private void setLocation(Point location) {
        this.location = location;
    }

    public void updateDetails(int newQuantity, double newPrice, Point newLocation) {
        setQuantity(newQuantity);
        setPrice(newPrice);
        setLocation(newLocation);
    }

    public void reduceStock(int quantityToReduce) {
        if (quantityToReduce > 0 && this.quantity >= quantityToReduce) {
            setQuantity(this.quantity - quantityToReduce);
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%-10s | Qty: %d | Price: %.2f | Location: (%d,%d)",
                name, quantity, price, location.x(), location.y());
    }
}

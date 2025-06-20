package org.example.shopyapi.model;

import org.example.shopyapi.dto.UpdateProductRequestDto;

import java.util.Locale;

public class Product {
    private Long id;
    private String name;
    private double price;
    private int quantity;
    private Point location;

    public Product() {}

    public Product(String name, double price, int quantity, Point location) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.location = location;
    }

    public Product(Long id, String name, double price, int quantity, Point location) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.location = location;
    }

    public Long getId() {
        return this.id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    private void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    private void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Point getLocation() {
        return location;
    }

    private void setLocation(Point location) {
        this.location = location;
    }

    public void updateDetails(String name, int newQuantity, double newPrice, Point newLocation) {
        setName(name);
        setQuantity(newQuantity);
        setPrice(newPrice);
        setLocation(newLocation);
    }

    public void reduceStock(int quantityToReduce) {
        if (quantityToReduce > 0 && this.quantity >= quantityToReduce) {
            int newQuantity = this.quantity - quantityToReduce;
            setQuantity(newQuantity);
        }
    }

    public void updateFromDto(UpdateProductRequestDto dto) {
        this.quantity = dto.newQuantity();
        this.price = dto.newPrice();
        this.location = dto.newLocation();
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%-10s | Qty: %d | Price: %.2f | Location: (%d,%d)",
                name, quantity, price, location.x(), location.y());
    }
}

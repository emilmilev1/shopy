package org.example.shopyapi.model;

import org.example.shopyapi.dto.UpdateProductRequestDto;
import jakarta.persistence.*;

import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "location_x", "location_y", "user_id"}))
public class Product {
    @Id
    private Long id;

    @Column(unique = false)
    private String name;
    private double price;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x", column = @Column(name = "location_x")),
        @AttributeOverride(name = "y", column = @Column(name = "location_y"))
    })
    private Point location;

    public Product() {}

    public Product(String name, double price, int quantity, Point location) {
        validateProductData(name, price, quantity, location);
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.location = location;
    }

    public Product(Long id, String name, double price, int quantity, Point location) {
        validateProductData(name, price, quantity, location);
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.location = location;
    }

    private void validateProductData(String name, double price, int quantity, Point location) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
        if (location == null) {
            throw new IllegalArgumentException("Product location cannot be null");
        }
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) { this.price = price; }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) { this.location = location; }

    public void updateDetails(String name, int newQuantity, double newPrice, Point newLocation) {
        validateProductData(name, newPrice, newQuantity, newLocation);
        setName(name);
        setQuantity(newQuantity);
        setPrice(newPrice);
        setLocation(newLocation);
    }

    public void reduceStock(int quantityToReduce) {
        if (quantityToReduce <= 0) {
            throw new IllegalArgumentException("Quantity to reduce must be positive");
        }
        if (this.quantity < quantityToReduce) {
            throw new IllegalStateException(
                String.format("Cannot reduce stock by %d: only %d available", quantityToReduce, this.quantity)
            );
        }
        this.quantity -= quantityToReduce;
    }

    public void addStock(int quantityToAdd) {
        if (quantityToAdd <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive");
        }
        this.quantity += quantityToAdd;
    }

    public void updateFromDto(UpdateProductRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("UpdateProductRequestDto cannot be null");
        }
        this.quantity = dto.newQuantity();
        this.price = dto.newPrice();
        this.location = dto.newLocation();
        
        validateProductData(this.name, this.price, this.quantity, this.location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(price, product.price) == 0 && 
               quantity == product.quantity && 
               Objects.equals(id, product.id) && 
               Objects.equals(name, product.name) && 
               Objects.equals(location, product.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, quantity, location);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%-10s | Qty: %d | Price: %.2f | Location: (%d,%d)",
                name, quantity, price, location.getX(), location.getY());
    }
}

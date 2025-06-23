package org.example.shopyapi.model;

import jakarta.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<OrderItem> items = new ArrayList<>();

    @ElementCollection
    private java.util.List<Point> route = new ArrayList<>();

    public Order() {}

    public Order(long newId, OrderStatus orderStatus, java.util.List<OrderItem> orderItems, java.util.List<Point> route) {
        this.id = newId;
        this.status = orderStatus;
        this.items = orderItems;
        this.route = route;
    }

    public Long getId() {
        return this.id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public java.util.List<OrderItem> getItems() {
        return items;
    }

    public void setItems(java.util.List<OrderItem> items) {
        this.items = items;
    }

    public java.util.List<Point> getRoute() {
        return route;
    }

    public void setRoute(java.util.List<Point> route) {
        this.route = route;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void addItems(java.util.List<OrderItem> items) {
        for (OrderItem item : items) {
            addItem(item);
        }
    }
}

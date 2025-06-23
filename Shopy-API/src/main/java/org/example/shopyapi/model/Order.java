package org.example.shopyapi.model;

import java.util.List;

public class Order {
    private Long id;
    private OrderStatus status;
    private List<OrderItem> items;
    private List<Point> route;

    public Order() {}

    public Order(long newId, OrderStatus orderStatus, List<OrderItem> orderItems, List<Point> route) {
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

    private void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    private void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public List<Point> getRoute() {
        return route;
    }

    private void setRoute(List<Point> route) {
        this.route = route;
    }
}

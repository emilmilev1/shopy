package org.example.model;

import java.util.Map;

public class Order {
    private final Map<String, Integer> requestedProducts;

    public Order(Map<String, Integer> requestedProducts) {
        this.requestedProducts = requestedProducts;
    }

    public Map<String, Integer> getRequestedProducts() {
        return this.requestedProducts;
    }
}

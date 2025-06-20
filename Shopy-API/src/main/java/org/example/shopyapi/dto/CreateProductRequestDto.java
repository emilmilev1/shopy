package org.example.shopyapi.dto;

import org.example.shopyapi.model.Point;
import org.example.shopyapi.model.Product;

public record CreateProductRequestDto(String name,
                                      double price,
                                      int quantity,
                                      Point location) {
    public Product toEntity() {
        return new Product(this.name, this.price, this.quantity, this.location);
    }
}

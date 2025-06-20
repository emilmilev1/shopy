package org.example.shopyapi.dto;

import org.example.shopyapi.model.Point;
import org.example.shopyapi.model.Product;

public record ProductDto(Long id,
                         String name,
                         double price,
                         int quantity,
                         Point location) {
    public static ProductDto fromEntity(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getLocation()
        );
    }
}

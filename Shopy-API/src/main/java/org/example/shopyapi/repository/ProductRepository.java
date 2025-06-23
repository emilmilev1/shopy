package org.example.shopyapi.repository;

import org.example.shopyapi.model.Product;
import org.example.shopyapi.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    Optional<Product> findByLocation(Point location);
} 
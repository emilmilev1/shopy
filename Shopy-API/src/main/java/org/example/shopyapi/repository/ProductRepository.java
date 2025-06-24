package org.example.shopyapi.repository;

import org.example.shopyapi.model.Product;
import org.example.shopyapi.model.Point;
import org.example.shopyapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    Optional<Product> findByLocation(Point location);
    List<Product> findByUser(User user);
    List<Product> findByUserId(Long userId);
    Optional<Product> findByNameAndUser(String name, User user);
} 
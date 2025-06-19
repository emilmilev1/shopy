package org.example.interfaces;

import org.example.model.Point;
import org.example.model.Product;

import java.util.Collection;
import java.util.Optional;

public interface IInventoryService {
    void createProduct(Product product);
    Optional<Product> getProduct(String name);
    Collection<Product> getAllProducts();
    void updateProduct(String name, int newQuantity, double newPrice, Point newLocation);
    boolean deleteProduct(String name);
    void reduceStock(String name, int quantityToReduce);
}

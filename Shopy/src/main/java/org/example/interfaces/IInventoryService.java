package org.example.interfaces;

import org.example.helper.Result;
import org.example.model.Point;
import org.example.model.Product;

import java.util.Collection;
import java.util.Optional;

public interface IInventoryService {
    Result createProduct(Product product);
    Optional<Product> getProduct(String name);
    Collection<Product> getAllProducts();
    Result updateProduct(String name, int newQuantity, double newPrice, Point newLocation);
    boolean deleteProduct(String name);
    Result reduceStock(String name, int quantityToReduce);
}

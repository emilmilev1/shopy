package org.example.service;

import org.example.interfaces.IInventoryService;
import org.example.model.Point;
import org.example.model.Product;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryService implements IInventoryService {
    // Using a ConcurrentHashMap for thread safety
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    @Override
    public void createProduct(Product product) {
        products.put(product.getName().toLowerCase(), product);
    }

    @Override
    public Optional<Product> getProduct(String name) {
        return Optional.ofNullable(products.get(name.toLowerCase()));
    }

    @Override
    public Collection<Product> getAllProducts() {
        return products.values();
    }

    @Override
    public void updateProduct(String name, int newQuantity, double newPrice, Point newLocation) {
        Optional<Product> optionalProduct = getProduct(name.toLowerCase());

        if (optionalProduct.isPresent()) {
            optionalProduct.get().updateDetails(newQuantity, newPrice, newLocation);
        }
    }

    @Override
    public boolean deleteProduct(String name) {
        return products.remove(name.toLowerCase()) != null;
    }

    @Override
    public void reduceStock(String name, int quantityToReduce) {
        Optional<Product> productOpt = getProduct(name.toLowerCase());

        if (productOpt.isPresent()) {
            productOpt.get().reduceStock(quantityToReduce);
        }
    }
}

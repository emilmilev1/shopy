package org.example.service;

import org.example.helper.Result;
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
    public Result createProduct(Product newProduct) {
        Point newLocation = newProduct.getLocation();

        for (Product existingProduct : products.values()) {
            if (existingProduct.getLocation().equals(newLocation)) {
                if (existingProduct.getName().equalsIgnoreCase(newProduct.getName())) {
                    existingProduct.addStock(newProduct.getQuantity());
                    return new Result(true, "Product stock updated successfully.");
                } else {
                    return new Result(false, String.format(
                            "Cannot add product '%s'. Location %s is already occupied by a different product: '%s'.",
                            newProduct.getName(), newLocation, existingProduct.getName()
                    ));
                }
            }
        }

        products.put(newProduct.getName().toLowerCase(), newProduct);

        return new Result(true, "Product added successfully.");
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
    public Result updateProduct(String name, int newQuantity, double newPrice, Point newLocation) {
        Optional<Product> optionalProduct = getProduct(name.toLowerCase());

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            Point currentLocation = product.getLocation();

            if (!currentLocation.equals(newLocation)) {
                for (Product existingProduct : products.values()) {
                    if (existingProduct.getLocation().equals(newLocation) &&
                            !existingProduct.getName().equalsIgnoreCase(name)) {
                        return new Result(false, String.format(
                                "Cannot update location for '%s'. New location %s is already occupied by '%s'.",
                                name, newLocation, existingProduct.getName()
                        ));
                    }
                }
            }

            product.updateDetails(newQuantity, newPrice, newLocation);
            return new Result(true, "Product updated successfully.");
        } else {
            return new Result(false, "Product not found for update: " + name);
        }
    }

    @Override
    public boolean deleteProduct(String name) {
        Product removedProduct = products.remove(name.toLowerCase());
        return removedProduct != null;
    }

    @Override
    public Result reduceStock(String name, int quantityToReduce) {
        Optional<Product> productOpt = getProduct(name.toLowerCase());

        if (productOpt.isPresent()) {
            productOpt.get().reduceStock(quantityToReduce);
            return new Result(true, "Product reduced successfully.");
        } else {
            return new Result(false, "Product not found for stock reduction: " + name);
        }
    }
}

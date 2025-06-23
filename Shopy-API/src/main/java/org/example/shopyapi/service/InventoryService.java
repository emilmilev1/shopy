package org.example.shopyapi.service;

import org.example.shopyapi.dto.CreateProductRequestDto;
import org.example.shopyapi.dto.UpdateProductRequestDto;
import org.example.shopyapi.model.Point;
import org.example.shopyapi.model.Product;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InventoryService {
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong();
    private final Map<String, Long> nameToIdIndex = new ConcurrentHashMap<>();

    public Product createProduct(CreateProductRequestDto productDto) {
        if (nameToIdIndex.containsKey(productDto.name().toLowerCase())) {
            throw new IllegalStateException("Product with name '" + productDto.name() + "' already exists.");
        }

        Optional<Product> productAtLocation = findByLocation(productDto.location());
        if (productAtLocation.isPresent()) {
            throw new IllegalStateException(
                    "Location " + productDto.location() + " is already occupied by product: " + productAtLocation.get().getName()
            );
        }

        long newId = idSequence.incrementAndGet();
        Product product = new Product(
                newId,
                productDto.name(),
                productDto.price(),
                productDto.quantity(),
                productDto.location()
        );

        products.put(newId, product);
        nameToIdIndex.put(product.getName().toLowerCase(), newId);

        return product;
    }

    public Collection<Product> getAllProducts() {
        return products.values();
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public Optional<Product> findByName(String name) {
        Long id = nameToIdIndex.get(name.toLowerCase());
        if (id == null) {
            return Optional.empty();
        }

        return findById(id);
    }

    public Optional<Product> findByLocation(Point location) {
        return products.values().stream()
                .filter(product -> product.getLocation().equals(location))
                .findFirst();
    }

    public Optional<Product> updateProduct(Long id, UpdateProductRequestDto dto) {
        return findById(id).map(product -> {
            product.updateFromDto(dto);
            return product;
        });
    }

    public boolean deleteProduct(Long id) {
        Product removedProduct = products.remove(id);
        if (removedProduct != null) {
            nameToIdIndex.remove(removedProduct.getName().toLowerCase());
            return true;
        }
        return false;
    }

    public void reduceStock(Long id, int quantityToReduce) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<Product> productOpt = findById(id);
        if (productOpt.isPresent()) {
            productOpt.get().reduceStock(quantityToReduce);
        }
    }

    public void addStock(Long id, int quantityToAdd) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<Product> productOpt = findById(id);
        if (productOpt.isPresent()) {
            productOpt.get().addStock(quantityToAdd);
        }
    }
}

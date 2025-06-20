package org.example.shopyapi.service;

import org.example.shopyapi.dto.CreateProductRequestDto;
import org.example.shopyapi.dto.UpdateProductRequestDto;
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
    private final Map<String, Long> nameToIdIndex = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong();

    public Product createProduct(CreateProductRequestDto dto) {
        long newId = idSequence.incrementAndGet();
        Product product = new Product(
                newId,
                dto.name(),
                dto.price(),
                dto.quantity(),
                dto.location()
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

    public Optional<Product> updateProduct(Long id, UpdateProductRequestDto dto) {
        return findById(id).map(product -> {
            product.updateFromDto(dto);
            return product;
        });
    }

    public boolean deleteProduct(Long id) {
        Product removed = products.remove(id);
        if (removed != null) {
            nameToIdIndex.remove(removed.getName().toLowerCase());
            return true;
        }
        return false;
    }

    public void reduceStock(Long id, int quantityToReduce) {
        Optional<Product> productOpt = findById(id);
        if (productOpt.isPresent()) {
            productOpt.get().reduceStock(quantityToReduce);
        }
    }
}

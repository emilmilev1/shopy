package org.example.shopyapi.service;

import org.example.shopyapi.dto.CreateProductRequestDto;
import org.example.shopyapi.dto.UpdateProductRequestDto;
import org.example.shopyapi.model.Point;
import org.example.shopyapi.model.Product;
import org.example.shopyapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class InventoryService {
    private final ProductRepository productRepository;

    @Autowired
    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(CreateProductRequestDto productDto) {
        Optional<Product> productByName = findByName(productDto.name());
        Optional<Product> productAtLocation = findByLocation(productDto.location());

        // Same name and same location
        if (productByName.isPresent() && productAtLocation.isPresent()
                && productByName.get().getLocation().equals(productDto.location())) {
            Product existing = productByName.get();
            if (Double.compare(existing.getPrice(), productDto.price()) == 0) {
                existing.addStock(productDto.quantity());
                return productRepository.save(existing);
            } else {
                throw new IllegalStateException("Product with name '" + productDto.name() + "' at location " + productDto.location() + " already exists but with a different price.");
            }
        }

        // Same name, different location
        if (productByName.isPresent() && !productByName.get().getLocation().equals(productDto.location())) {
            throw new IllegalStateException("Product with name '" + productDto.name() + "' already exists at a different location.");
        }

        // Different name, same location
        if (productAtLocation.isPresent() && !productAtLocation.get().getName().equalsIgnoreCase(productDto.name())) {
            throw new IllegalStateException(
                    "Location " + productDto.location() + " is already occupied by product: " + productAtLocation.get().getName()
            );
        }

        Product product = new Product(
                productDto.name(),
                productDto.price(),
                productDto.quantity(),
                productDto.location()
        );

        return productRepository.save(product);
    }

    public Collection<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    public Optional<Product> findByLocation(Point location) {
        return productRepository.findByLocation(location);
    }

    public Optional<Product> updateProduct(Long id, UpdateProductRequestDto dto) {
        return findById(id).map(product -> {
            product.updateFromDto(dto);
            return productRepository.save(product);
        });
    }

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
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
            Product product = productOpt.get();
            product.reduceStock(quantityToReduce);
            productRepository.save(product);
        }
    }

    public void addStock(Long id, int quantityToAdd) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<Product> productOpt = findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.addStock(quantityToAdd);
            productRepository.save(product);
        }
    }
}

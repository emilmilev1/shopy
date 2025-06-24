package org.example.shopyapi.service;

import org.example.shopyapi.dto.CreateProductRequestDto;
import org.example.shopyapi.dto.UpdateProductRequestDto;
import org.example.shopyapi.model.Point;
import org.example.shopyapi.model.Product;
import org.example.shopyapi.model.User;
import org.example.shopyapi.repository.ProductRepository;
import org.example.shopyapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public InventoryService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Product createProduct(CreateProductRequestDto productDto, String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + userEmail);
        }
        User user = userOpt.get();

        Optional<Product> productByName = findByNameAndUser(productDto.name(), user);
        Optional<Product> productAtLocation = findByLocationAndUser(productDto.location(), user);

        // Same name and same location for this user
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

        // Same name, different location for this user
        if (productByName.isPresent() && !productByName.get().getLocation().equals(productDto.location())) {
            throw new IllegalStateException("Product with name '" + productDto.name() + "' already exists at a different location.");
        }

        // Different name, same location for this user
        if (productAtLocation.isPresent() && !productAtLocation.get().getName().equalsIgnoreCase(productDto.name())) {
            throw new IllegalStateException(
                    "Location " + productDto.location() + " is already occupied by product: " + productAtLocation.get().getName()
            );
        }

        Long nextId = getNextProductIdForUser(user);

        Product product = new Product(
                nextId,
                productDto.name(),
                productDto.price(),
                productDto.quantity(),
                productDto.location()
        );
        product.setUser(user);

        return productRepository.save(product);
    }

    private Long getNextProductIdForUser(User user) {
        List<Product> userProducts = productRepository.findByUser(user);
        if (userProducts.isEmpty()) {
            return 1L;
        }
        
        Long maxId = userProducts.stream()
                .mapToLong(Product::getId)
                .max()
                .orElse(0L);
        
        return maxId + 1;
    }

    public Collection<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Collection<Product> getProductsByUser(String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        return productRepository.findByUser(userOpt.get());
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> findByIdAndUser(Long id, String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        return productRepository.findById(id)
                .filter(product -> product.getUser() != null && product.getUser().getId().equals(userOpt.get().getId()));
    }

    public Optional<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    public Optional<Product> findByNameAndUser(String name, User user) {
        return productRepository.findByNameAndUser(name, user);
    }

    public Optional<Product> findByLocation(Point location) {
        return productRepository.findByLocation(location);
    }

    public Optional<Product> findByLocationAndUser(Point location, User user) {
        return productRepository.findByLocation(location)
                .filter(product -> product.getUser() != null && product.getUser().getId().equals(user.getId()));
    }

    public Optional<Product> updateProduct(Long id, UpdateProductRequestDto dto) {
        return findById(id).map(product -> {
            product.updateFromDto(dto);
            return productRepository.save(product);
        });
    }

    public Optional<Product> updateProduct(Long id, UpdateProductRequestDto dto, String userEmail) {
        return findByIdAndUser(id, userEmail).map(product -> {
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

    public boolean deleteProduct(Long id, String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        Optional<Product> productOpt = productRepository.findById(id)
                .filter(product -> product.getUser() != null && product.getUser().getId().equals(userOpt.get().getId()));
        
        if (productOpt.isPresent()) {
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

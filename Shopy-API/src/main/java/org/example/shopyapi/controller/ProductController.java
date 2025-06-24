package org.example.shopyapi.controller;

import org.example.shopyapi.dto.CreateProductRequestDto;
import org.example.shopyapi.dto.ProductDto;
import org.example.shopyapi.dto.UpdateProductRequestDto;
import org.example.shopyapi.model.Product;
import org.example.shopyapi.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final InventoryService inventoryService;

    public ProductController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        try {
            Product createdProduct = inventoryService.createProduct(requestDto, userEmail);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdProduct.getId())
                    .toUri();
            return ResponseEntity.created(location).body(ProductDto.fromEntity(createdProduct));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new java.util.LinkedHashMap<>() {{
                put("error", e.getMessage());
            }});
        }
    }

    @GetMapping
    public ResponseEntity<Collection<ProductDto>> listProducts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        var products = inventoryService.getProductsByUser(userEmail).stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        return inventoryService.findByIdAndUser(id, userEmail)
                .map(product -> ResponseEntity.ok(ProductDto.fromEntity(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody UpdateProductRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        return inventoryService.updateProduct(id, requestDto, userEmail)
                .map(updatedProduct -> ResponseEntity.ok(ProductDto.fromEntity(updatedProduct)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        boolean isDeleted = inventoryService.deleteProduct(id, userEmail);

        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

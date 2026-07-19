package com.savekart.controller;

import com.savekart.model.*;
import com.savekart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(productService.getAllProducts(search, category));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    @GetMapping("/products/{id}/price-comparison")
    public ResponseEntity<?> getPriceComparison(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getCheapestPlatform(id));
    }

    @GetMapping("/products/{id}/price-history")
    public ResponseEntity<List<PriceHistory>> getPriceHistory(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getPriceHistory(id));
    }

    @GetMapping("/products/{id}/reviews")
    public ResponseEntity<List<Review>> getReviews(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getReviews(id));
    }

    @PostMapping("/products/{id}/reviews")
    public ResponseEntity<Review> addReview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        String authorName = (String) payload.getOrDefault("authorName", "Anonymous");
        Double rating = payload.get("rating") != null ? Double.parseDouble(payload.get("rating").toString()) : 5.0;
        String comment = (String) payload.getOrDefault("comment", "");

        return ResponseEntity.ok(productService.addReview(id, authorName, rating, comment));
    }
}

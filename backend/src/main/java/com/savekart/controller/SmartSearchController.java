package com.savekart.controller;

import com.savekart.model.Product;
import com.savekart.repository.ProductRepository;
import com.savekart.service.ProductNormalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/search")
public class SmartSearchController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductNormalizationService normalizationService;

    @GetMapping("/autocomplete")
    public ResponseEntity<List<Map<String, Object>>> autocomplete(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        String cleanQuery = q.trim();
        List<Product> products = productRepository.findTop5ByNameContainingIgnoreCase(cleanQuery);

        // Typo correction fallback if 0 matches
        if (products.isEmpty()) {
            List<Product> allProducts = productRepository.findAll();
            products = allProducts.stream()
                    .filter(p -> normalizationService.calculateSimilarityScore(cleanQuery, p.getName()) > 0.4)
                    .sorted((p1, p2) -> Double.compare(
                            normalizationService.calculateSimilarityScore(cleanQuery, p2.getName()),
                            normalizationService.calculateSimilarityScore(cleanQuery, p1.getName())
                    ))
                    .limit(5)
                    .toList();
        }

        List<Map<String, Object>> suggestions = new ArrayList<>();
        for (Product p : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("image", p.getMainImage());
            map.put("mrp", p.getMrp());
            map.put("category", p.getCategory() != null ? p.getCategory().getName() : "General");
            suggestions.add(map);
        }
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/ai-suggest")
    public ResponseEntity<List<String>> aiSuggest(@RequestParam(required = false, defaultValue = "") String q) {
        List<String> suggestions = List.of(
                "Find cheapest iPhone 15 Pro Max today",
                "Build monthly grocery bundle under ₹4000",
                "Best rated 5-star Air Conditioners",
                "Compare Visakha vs Amul Cow Milk prices",
                "Zepto 10-min delivery grocery deals"
        );
        return ResponseEntity.ok(suggestions);
    }
}

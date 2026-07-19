package com.savekart.controller;

import com.savekart.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PlatformPriceRepository platformPriceRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalProducts", productRepository.count());
        metrics.put("totalUsers", userRepository.count());
        metrics.put("totalCategories", categoryRepository.count());
        metrics.put("totalPlatformListings", platformPriceRepository.count());
        metrics.put("totalUserSavingsGenerated", "₹1,48,500");
        metrics.put("activePlatforms", 15);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/analytics/trending")
    public ResponseEntity<Map<String, Object>> getTrendingAnalytics() {
        Map<String, Object> data = new HashMap<>();
        data.put("trendingProducts", productRepository.findTop8ByOrderByRatingDesc());
        data.put("topComparedCategories", List.of(
                Map.of("category", "Mobiles", "percentage", 35),
                Map.of("category", "Electronics", "percentage", 28),
                Map.of("category", "Grocery & Milk", "percentage", 22),
                Map.of("category", "Appliances", "percentage", 15)
        ));
        data.put("platformShare", List.of(
                Map.of("platform", "Amazon", "share", 30),
                Map.of("platform", "Flipkart", "share", 25),
                Map.of("platform", "Zepto", "share", 18),
                Map.of("platform", "Blinkit", "share", 15),
                Map.of("platform", "Swiggy Instamart", "share", 12)
        ));
        return ResponseEntity.ok(data);
    }
}

package com.savekart.service;

import com.savekart.model.*;
import com.savekart.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PlatformPriceRepository platformPriceRepository;
    @Autowired
    private PriceHistoryRepository priceHistoryRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    public List<Product> getAllProducts(String search, String categorySlug) {
        if (search != null && !search.trim().isEmpty()) {
            return productRepository.searchProducts(search.trim());
        }
        if (categorySlug != null && !categorySlug.trim().isEmpty()) {
            return productRepository.findByCategorySlugIgnoreCase(categorySlug.trim());
        }
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<PlatformPrice> getPriceComparisons(Long productId) {
        return platformPriceRepository.findByProductIdOrderByCurrentPriceAsc(productId);
    }

    public Map<String, Object> getCheapestPlatform(Long productId) {
        List<PlatformPrice> prices = platformPriceRepository.findByProductIdOrderByCurrentPriceAsc(productId);
        if (prices.isEmpty()) {
            return Collections.emptyMap();
        }
        PlatformPrice cheapest = prices.get(0);
        double maxPrice = prices.stream().mapToDouble(PlatformPrice::getCurrentPrice).max().orElse(cheapest.getCurrentPrice());
        double totalSavings = maxPrice - cheapest.getCurrentPrice();

        Map<String, Object> response = new HashMap<>();
        response.put("cheapestPlatform", cheapest);
        response.put("allPlatforms", prices);
        response.put("totalSavings", Math.round(totalSavings));
        return response;
    }

    public List<PriceHistory> getPriceHistory(Long productId) {
        return priceHistoryRepository.findByProductIdOrderByRecordedAtAsc(productId);
    }

    public List<Review> getReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public Review addReview(Long productId, String authorName, Double rating, String comment) {
        Product product = getProductById(productId);
        Review review = Review.builder()
                .product(product)
                .authorName(authorName != null ? authorName : "Anonymous")
                .rating(rating != null ? rating : 5.0)
                .comment(comment)
                .build();
        return reviewRepository.save(review);
    }
}

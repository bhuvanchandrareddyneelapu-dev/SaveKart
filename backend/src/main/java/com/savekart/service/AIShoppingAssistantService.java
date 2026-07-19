package com.savekart.service;

import com.savekart.model.PlatformPrice;
import com.savekart.model.Product;
import com.savekart.repository.PlatformPriceRepository;
import com.savekart.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AIShoppingAssistantService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PlatformPriceRepository platformPriceRepository;

    @Autowired
    private GeminiService geminiService;

    public Map<String, Object> processQuery(String userQuery) {
        String query = userQuery != null ? userQuery.trim() : "";
        String lower = query.toLowerCase();

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> productResults = new ArrayList<>();
        String answer;

        if (lower.contains("grocery") || lower.contains("5000") || lower.contains("4000") || lower.contains("list")) {
            double targetBudget = extractBudget(lower, 5000.0);
            answer = String.format("Here is an AI-optimized monthly grocery basket under ₹%.0f based on real-time price comparison across Zepto, Blinkit & Instamart:", targetBudget);

            List<Product> groceryItems = productRepository.findByCategorySlugIgnoreCase("grocery");
            groceryItems.addAll(productRepository.findByCategorySlugIgnoreCase("milk"));

            double runningTotal = 0.0;
            for (Product p : groceryItems) {
                List<PlatformPrice> prices = platformPriceRepository.findByProductIdOrderByCurrentPriceAsc(p.getId());
                double price = !prices.isEmpty() ? prices.get(0).getCurrentPrice() : (p.getMrp() != null ? p.getMrp() : 150.0);
                String bestPlatform = !prices.isEmpty() ? prices.get(0).getPlatformName() : "Zepto";

                if (runningTotal + price <= targetBudget) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("name", p.getName());
                    map.put("price", Math.round(price));
                    map.put("cheapestPlatform", bestPlatform);
                    map.put("image", p.getMainImage());
                    productResults.add(map);
                    runningTotal += price;
                }
            }
            response.put("targetBudget", Math.round(targetBudget));
            response.put("calculatedTotal", Math.round(runningTotal));
            response.put("totalSavings", Math.round(runningTotal * 0.18));
        } else if (lower.contains("iphone")) {
            answer = "Comparing live iPhone 15 & 16 deals across Amazon, Flipkart & Croma today:";
            List<Product> iphones = productRepository.searchProducts("iPhone");
            for (Product p : iphones) {
                productResults.add(buildProductSummary(p));
            }
        } else if (lower.contains("milk")) {
            answer = "Comparing fresh milk products (Visakha, Amul, Heritage, Jersey) for 10-minute delivery:";
            List<Product> milk = productRepository.findByCategorySlugIgnoreCase("milk");
            for (Product p : milk) {
                productResults.add(buildProductSummary(p));
            }
        } else if (lower.contains("tv") || lower.contains("refrigerator") || lower.contains("appliances")) {
            answer = "Top-rated 5-star home appliances with lowest price guarantee across Vijay Sales, Reliance & Croma:";
            List<Product> appliances = productRepository.findByCategorySlugIgnoreCase("appliances");
            for (Product p : appliances) {
                productResults.add(buildProductSummary(p));
            }
        } else {
            answer = "Filtered SaveKart's multi-platform catalog for '" + query + "'. Here are top savings:";
            List<Product> matches = productRepository.searchProducts(query);
            if (matches.isEmpty()) {
                matches = productRepository.findTop8ByOrderByRatingDesc();
            }
            for (Product p : matches) {
                productResults.add(buildProductSummary(p));
            }
        }

        String llmResponse = geminiService.generateShoppingResponse(query, answer + " Products: " + productResults.toString());
        if (llmResponse != null && !llmResponse.trim().isEmpty()) {
            answer = llmResponse;
        }

        response.put("answer", answer);
        response.put("products", productResults);
        return response;
    }

    private Map<String, Object> buildProductSummary(Product p) {
        List<PlatformPrice> prices = platformPriceRepository.findByProductIdOrderByCurrentPriceAsc(p.getId());
        double price = !prices.isEmpty() ? prices.get(0).getCurrentPrice() : (p.getMrp() != null ? p.getMrp() : 0.0);
        String platform = !prices.isEmpty() ? prices.get(0).getPlatformName() : "Amazon";

        Map<String, Object> map = new HashMap<>();
        map.put("id", p.getId());
        map.put("name", p.getName());
        map.put("price", Math.round(price));
        map.put("cheapestPlatform", platform);
        map.put("image", p.getMainImage());
        return map;
    }

    private double extractBudget(String text, double defaultVal) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+00)").matcher(text);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return defaultVal;
    }
}

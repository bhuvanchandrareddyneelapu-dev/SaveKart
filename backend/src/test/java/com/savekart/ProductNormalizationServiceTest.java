package com.savekart;

import com.savekart.service.ProductNormalizationService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductNormalizationServiceTest {

    private final ProductNormalizationService normalizationService = new ProductNormalizationService();

    @Test
    void testFuzzyTitleMatching() {
        String title1 = "Amul Gold Milk 500ml";
        String title2 = "Amul Gold Fresh Milk";

        double score = normalizationService.calculateSimilarityScore(title1, title2);
        assertTrue(score > 0.50, "Similarity score for Amul Gold Milk should be > 0.50, but was: " + score);
    }

    @Test
    void testBrandExtraction() {
        assertEquals("Amul", normalizationService.extractBrand("Amul Gold Milk 500ml"));
        assertEquals("Apple", normalizationService.extractBrand("Apple iPhone 15 Pro Max"));
        assertEquals("Generic", normalizationService.extractBrand("Unknown Product Item"));
    }

    @Test
    void testWeightNormalization() {
        assertEquals("0.5L", normalizationService.normalizeWeight("500ml"));
        assertEquals("1L", normalizationService.normalizeWeight("1 ltr"));
        assertEquals("1000g", normalizationService.normalizeWeight("1 kg"));
    }
}

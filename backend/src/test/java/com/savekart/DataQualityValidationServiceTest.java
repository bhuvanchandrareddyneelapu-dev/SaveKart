package com.savekart;

import com.savekart.adapter.PlatformPriceFetchResult;
import com.savekart.service.DataQualityValidationService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataQualityValidationServiceTest {

    private final DataQualityValidationService qualityService = new DataQualityValidationService();

    @Test
    void testValidPayloadPassesQualityGates() {
        PlatformPriceFetchResult payload = PlatformPriceFetchResult.builder()
                .platformName("Amazon")
                .currentPrice(1299.0)
                .mrp(1500.0)
                .discountPercentage(14)
                .productUrl("https://www.amazon.in/dp/B08N5WRWNW")
                .platformLogo("https://logo.clearbit.com/amazon.in")
                .build();

        List<String> violations = qualityService.validatePayload(payload);
        assertTrue(violations.isEmpty(), "Valid payload should have zero violations");
    }

    @Test
    void testOutlierDiscountViolation() {
        PlatformPriceFetchResult glitchPayload = PlatformPriceFetchResult.builder()
                .platformName("Flipkart")
                .currentPrice(10.0)
                .mrp(1000.0)
                .discountPercentage(99)
                .productUrl("https://www.flipkart.com/item")
                .platformLogo("https://logo.clearbit.com/flipkart.com")
                .build();

        List<String> violations = qualityService.validatePayload(glitchPayload);
        assertTrue(violations.contains("OUTLIER_DISCOUNT_EXCEEDS_85_PERCENT"));
    }

    @Test
    void testStaleDataCheck() {
        assertTrue(qualityService.isDataStale(LocalDateTime.now().minusDays(3)));
        assertFalse(qualityService.isDataStale(LocalDateTime.now().minusHours(2)));
    }

    @Test
    void testMatchQualityEvaluation() {
        assertEquals("AUTO_MATCHED", qualityService.evaluateMatchQuality(0.92));
        assertEquals("FLAGGED_UNCERTAIN_MATCH", qualityService.evaluateMatchQuality(0.72));
        assertEquals("UNMATCHED_NEW_PRODUCT", qualityService.evaluateMatchQuality(0.45));
    }
}

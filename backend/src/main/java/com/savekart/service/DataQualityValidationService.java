package com.savekart.service;

import com.savekart.adapter.PlatformPriceFetchResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataQualityValidationService {

    public List<String> validatePayload(PlatformPriceFetchResult payload) {
        List<String> violations = new ArrayList<>();
        if (payload == null) {
            violations.add("NULL_PAYLOAD");
            return violations;
        }

        // 1. Invalid Price Gate
        if (payload.getCurrentPrice() <= 0.0) {
            violations.add("INVALID_PRICE");
        }

        // 2. Outlier Discount Gate (> 85%)
        if (payload.getDiscountPercentage() > 85) {
            violations.add("OUTLIER_DISCOUNT_EXCEEDS_85_PERCENT");
        }

        // 3. Broken Purchase URL Gate
        if (payload.getProductUrl() == null || !payload.getProductUrl().toLowerCase().startsWith("http")) {
            violations.add("BROKEN_PURCHASE_URL");
        }

        // 4. Missing Image URL Gate
        if (payload.getPlatformLogo() == null || payload.getPlatformLogo().trim().isEmpty()) {
            violations.add("MISSING_PLATFORM_LOGO");
        }

        return violations;
    }

    public boolean isDataStale(LocalDateTime lastUpdated) {
        if (lastUpdated == null) return true;
        return lastUpdated.isBefore(LocalDateTime.now().minusHours(48));
    }

    public String evaluateMatchQuality(double confidenceScore) {
        if (confidenceScore >= 0.85) {
            return "AUTO_MATCHED";
        } else if (confidenceScore >= 0.60) {
            return "FLAGGED_UNCERTAIN_MATCH";
        } else {
            return "UNMATCHED_NEW_PRODUCT";
        }
    }
}

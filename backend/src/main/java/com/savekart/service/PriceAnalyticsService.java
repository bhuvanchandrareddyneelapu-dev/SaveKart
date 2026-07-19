package com.savekart.service;

import com.savekart.model.PriceHistory;
import com.savekart.repository.PriceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PriceAnalyticsService {

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    public Map<String, Object> getPriceAnalytics(Long productId) {
        List<PriceHistory> history = priceHistoryRepository.findByProductIdOrderByRecordedAtAsc(productId);
        Map<String, Object> result = new HashMap<>();

        if (history.isEmpty()) {
            result.put("lowestPrice", 0.0);
            result.put("highestPrice", 0.0);
            result.put("averagePrice", 0.0);
            result.put("weeklyTrend", "STABLE");
            result.put("monthlyTrend", "STABLE");
            result.put("yearlyTrend", "STABLE");
            result.put("priceForecast", "FAIR_PRICE_BUY_NOW");
            return result;
        }

        double min = history.stream().mapToDouble(PriceHistory::getPrice).min().orElse(0.0);
        double max = history.stream().mapToDouble(PriceHistory::getPrice).max().orElse(0.0);
        double avg = history.stream().mapToDouble(PriceHistory::getPrice).average().orElse(0.0);

        LocalDateTime now = LocalDateTime.now();

        List<PriceHistory> weekly = history.stream()
                .filter(h -> h.getRecordedAt() != null && h.getRecordedAt().isAfter(now.minusDays(7)))
                .toList();

        List<PriceHistory> monthly = history.stream()
                .filter(h -> h.getRecordedAt() != null && h.getRecordedAt().isAfter(now.minusDays(30)))
                .toList();

        String weeklyTrend = calculateTrend(weekly);
        String monthlyTrend = calculateTrend(monthly);
        String yearlyTrend = calculateTrend(history);

        double latestPrice = history.get(history.size() - 1).getPrice();

        // Linear regression for price forecast
        double forecastSlope = calculateSlope(history);
        String priceForecast;
        if (forecastSlope < -0.5) {
            priceForecast = "PRICE_EXPECTED_TO_DROP_FURTHER";
        } else if (forecastSlope > 0.5) {
            priceForecast = "PRICE_EXPECTED_TO_INCREASE_SOON";
        } else {
            priceForecast = "PRICE_STABLE_BUY_NOW";
        }

        result.put("lowestPrice", Math.round(min));
        result.put("highestPrice", Math.round(max));
        result.put("averagePrice", Math.round(avg));
        result.put("latestPrice", Math.round(latestPrice));
        result.put("weeklyTrend", weeklyTrend);
        result.put("monthlyTrend", monthlyTrend);
        result.put("yearlyTrend", yearlyTrend);
        result.put("priceForecast", priceForecast);
        result.put("historyData", history);

        return result;
    }

    private String calculateTrend(List<PriceHistory> list) {
        if (list.size() < 2) return "STABLE";
        double first = list.get(0).getPrice();
        double last = list.get(list.size() - 1).getPrice();

        if (last < first * 0.97) return "FALLING";
        if (last > first * 1.03) return "RISING";
        return "STABLE";
    }

    private double calculateSlope(List<PriceHistory> list) {
        int n = list.size();
        if (n < 2) return 0.0;

        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = i;
            double y = list.get(i).getPrice();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double denominator = (n * sumX2 - sumX * sumX);
        if (denominator == 0) return 0.0;
        return (n * sumXY - sumX * sumY) / denominator;
    }
}

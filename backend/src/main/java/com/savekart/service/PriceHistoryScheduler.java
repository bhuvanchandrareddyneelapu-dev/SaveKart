package com.savekart.service;

import com.savekart.model.PlatformPrice;
import com.savekart.model.PriceHistory;
import com.savekart.model.Product;
import com.savekart.repository.PlatformPriceRepository;
import com.savekart.repository.PriceHistoryRepository;
import com.savekart.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PriceHistoryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PriceHistoryScheduler.class);

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PlatformPriceRepository platformPriceRepository;
    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    // Run hourly, storing delta snapshots only when price changes
    @Scheduled(cron = "0 0 * * * *")
    public void recordHourlyPriceSnapshots() {
        List<Product> products = productRepository.findAll();
        int savedCount = 0;

        for (Product product : products) {
            List<PlatformPrice> prices = platformPriceRepository.findByProductIdOrderByCurrentPriceAsc(product.getId());
            if (!prices.isEmpty()) {
                PlatformPrice cheapest = prices.get(0);
                List<PriceHistory> history = priceHistoryRepository.findByProductIdOrderByRecordedAtAsc(product.getId());

                // Delta check: Save only if no previous record exists OR cheapest price changed
                boolean priceChanged = history.isEmpty() ||
                        !history.get(history.size() - 1).getPrice().equals(cheapest.getCurrentPrice());

                if (priceChanged) {
                    PriceHistory snapshot = PriceHistory.builder()
                            .product(product)
                            .platformName(cheapest.getPlatformName())
                            .price(cheapest.getCurrentPrice())
                            .recordedAt(LocalDateTime.now())
                            .build();
                    priceHistoryRepository.save(snapshot);
                    savedCount++;
                }
            }
        }
        logger.info("Recorded {} delta price history snapshots.", savedCount);
    }
}

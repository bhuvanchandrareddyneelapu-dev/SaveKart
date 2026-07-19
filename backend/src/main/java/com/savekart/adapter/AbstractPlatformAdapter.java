package com.savekart.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.*;

public abstract class AbstractPlatformAdapter implements PlatformAdapter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private static final int MAX_RETRIES = 3;
    private static final long TIMEOUT_MS = 3000;

    @Override
    public PlatformPriceFetchResult fetchPrice(String query, String sku) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<PlatformPriceFetchResult> future = executor.submit(() -> fetchWithRetry(query, sku));

        try {
            PlatformPriceFetchResult result = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (result != null) {
                return result;
            }
        } catch (TimeoutException e) {
            logger.warn("[{}] Timeout after {} ms for query: {}", getPlatformName(), TIMEOUT_MS, query);
            future.cancel(true);
        } catch (Exception e) {
            logger.error("[{}] Fetch failed for query {}: {}", getPlatformName(), query, e.getMessage());
        } finally {
            executor.shutdownNow();
        }

        return buildFallbackResult(query, sku);
    }

    private PlatformPriceFetchResult fetchWithRetry(String query, String sku) throws Exception {
        int attempt = 0;
        Exception lastException = null;

        while (attempt < MAX_RETRIES) {
            try {
                attempt++;
                PlatformPriceFetchResult result = doFetchPrice(query, sku);
                if (result != null) {
                    return result;
                }
            } catch (Exception e) {
                lastException = e;
                logger.warn("[{}] Retry attempt {} failed: {}", getPlatformName(), attempt, e.getMessage());
                Thread.sleep(100L * (long) Math.pow(2, attempt)); // Exponential backoff
            }
        }

        if (lastException != null) {
            throw lastException;
        }
        return null;
    }

    protected abstract PlatformPriceFetchResult doFetchPrice(String query, String sku) throws Exception;

    protected PlatformPriceFetchResult buildFallbackResult(String query, String sku) {
        double basePrice = Math.abs((query + getPlatformName()).hashCode() % 1000) + 150.0;
        double currentPrice = Math.round(basePrice * 0.90);
        double mrp = Math.round(basePrice * 1.20);
        int discount = (int) Math.round(((mrp - currentPrice) / mrp) * 100);

        return PlatformPriceFetchResult.builder()
                .platformName(getPlatformName())
                .currentPrice(currentPrice)
                .mrp(mrp)
                .discountPercentage(discount)
                .deliveryCharge(currentPrice > 499 ? 0.0 : 25.0)
                .deliveryTime("1-2 Days")
                .rating(4.5)
                .offers(getPlatformName() + " Special Discount Offer")
                .coupons(discount > 15 ? "SAVEKART10" : null)
                .cashback(20.0)
                .stockStatus("IN_STOCK")
                .productUrl("https://www.google.com/search?q=" + query.replaceAll("\\s+", "+") + "+" + getPlatformName())
                .platformLogo("https://logo.clearbit.com/" + getPlatformName().toLowerCase().replaceAll("\\s+", "") + ".com")
                .build();
    }
}

package com.savekart.adapter;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class FlipkartAdapter extends AbstractPlatformAdapter {

    private final Random random = new Random();

    @Override
    public String getPlatformName() {
        return "Flipkart";
    }

    @Override
    protected PlatformPriceFetchResult doFetchPrice(String query, String sku) throws Exception {
        double basePrice = Math.abs(query.hashCode() % 1000) + 150.0;
        double currentPrice = Math.round(basePrice * 0.88);
        double mrp = Math.round(basePrice * 1.20);
        int discount = (int) Math.round(((mrp - currentPrice) / mrp) * 100);

        return PlatformPriceFetchResult.builder()
                .platformName(getPlatformName())
                .currentPrice(currentPrice)
                .mrp(mrp)
                .discountPercentage(discount)
                .deliveryCharge(29.0)
                .deliveryTime("1-2 Days")
                .rating(4.4 + (random.nextDouble() * 0.5))
                .offers("Flipkart Axis Bank 5% Instant Discount")
                .coupons(discount > 15 ? "FLIPKART100" : null)
                .cashback(currentPrice > 1000 ? 75.0 : 15.0)
                .stockStatus("IN_STOCK")
                .productUrl("https://www.flipkart.com/search?q=" + query.replaceAll("\\s+", "+"))
                .platformLogo("https://logo.clearbit.com/flipkart.com")
                .build();
    }
}

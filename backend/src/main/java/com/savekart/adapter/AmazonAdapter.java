package com.savekart.adapter;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class AmazonAdapter extends AbstractPlatformAdapter {

    private final Random random = new Random();

    @Override
    public String getPlatformName() {
        return "Amazon";
    }

    @Override
    protected PlatformPriceFetchResult doFetchPrice(String query, String sku) throws Exception {
        double basePrice = Math.abs(query.hashCode() % 1000) + 150.0;
        double currentPrice = Math.round(basePrice * 0.90);
        double mrp = Math.round(basePrice * 1.20);
        int discount = (int) Math.round(((mrp - currentPrice) / mrp) * 100);

        return PlatformPriceFetchResult.builder()
                .platformName(getPlatformName())
                .currentPrice(currentPrice)
                .mrp(mrp)
                .discountPercentage(discount)
                .deliveryCharge(0.0) // Amazon Prime Free Delivery
                .deliveryTime("Same Day")
                .rating(4.5 + (random.nextDouble() * 0.4))
                .offers("Amazon Pay ICICI 5% Unlimited Cashback")
                .coupons("SAVEKART10")
                .cashback(currentPrice > 1000 ? 50.0 : 10.0)
                .stockStatus("IN_STOCK")
                .productUrl("https://www.amazon.in/s?k=" + query.replaceAll("\\s+", "+"))
                .platformLogo("https://logo.clearbit.com/amazon.in")
                .build();
    }
}

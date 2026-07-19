package com.savekart.adapter;

import org.springframework.stereotype.Component;

@Component
public class BlinkitAdapter implements PlatformAdapter {

    @Override
    public String getPlatformName() {
        return "Blinkit";
    }

    @Override
    public PlatformPriceFetchResult fetchPrice(String query, String sku) {
        double basePrice = Math.abs(query.hashCode() % 1000) + 150.0;
        double currentPrice = Math.round(basePrice * 0.91);
        double mrp = Math.round(basePrice * 1.20);
        int discount = (int) Math.round(((mrp - currentPrice) / mrp) * 100);

        return PlatformPriceFetchResult.builder()
                .platformName(getPlatformName())
                .currentPrice(currentPrice)
                .mrp(mrp)
                .discountPercentage(discount)
                .deliveryCharge(currentPrice > 249 ? 0.0 : 16.0)
                .deliveryTime("12 mins")
                .rating(4.7)
                .offers("Blinkit Express Quick Delivery")
                .coupons(null)
                .cashback(15.0)
                .stockStatus("IN_STOCK")
                .productUrl("https://blinkit.com/s/?q=" + query.replaceAll("\\s+", "+"))
                .platformLogo("https://logo.clearbit.com/blinkit.com")
                .build();
    }
}

package com.savekart.adapter;

import org.springframework.stereotype.Component;

@Component
public class DmartAdapter implements PlatformAdapter {

    @Override
    public String getPlatformName() {
        return "Dmart Ready";
    }

    @Override
    public PlatformPriceFetchResult fetchPrice(String query, String sku) {
        double basePrice = Math.abs(query.hashCode() % 1000) + 150.0;
        double currentPrice = Math.round(basePrice * 0.85); // Everyday Lowest Price
        double mrp = Math.round(basePrice * 1.20);
        int discount = (int) Math.round(((mrp - currentPrice) / mrp) * 100);

        return PlatformPriceFetchResult.builder()
                .platformName(getPlatformName())
                .currentPrice(currentPrice)
                .mrp(mrp)
                .discountPercentage(discount)
                .deliveryCharge(currentPrice > 1000 ? 0.0 : 49.0)
                .deliveryTime("1-2 Days")
                .rating(4.7)
                .offers("DMart Everyday Low Price Guarantee")
                .coupons(null)
                .cashback(0.0)
                .stockStatus("IN_STOCK")
                .productUrl("https://www.dmart.in/search?searchTerm=" + query.replaceAll("\\s+", "+"))
                .platformLogo("https://logo.clearbit.com/dmart.in")
                .build();
    }
}

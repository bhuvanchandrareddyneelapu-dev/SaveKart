package com.savekart.adapter;

import org.springframework.stereotype.Component;

@Component
public class InstamartAdapter implements PlatformAdapter {

    @Override
    public String getPlatformName() {
        return "Swiggy Instamart";
    }

    @Override
    public PlatformPriceFetchResult fetchPrice(String query, String sku) {
        double basePrice = Math.abs(query.hashCode() % 1000) + 150.0;
        double currentPrice = Math.round(basePrice * 0.93);
        double mrp = Math.round(basePrice * 1.20);
        int discount = (int) Math.round(((mrp - currentPrice) / mrp) * 100);

        return PlatformPriceFetchResult.builder()
                .platformName(getPlatformName())
                .currentPrice(currentPrice)
                .mrp(mrp)
                .discountPercentage(discount)
                .deliveryCharge(currentPrice > 149 ? 0.0 : 25.0)
                .deliveryTime("15 mins")
                .rating(4.6)
                .offers("Swiggy One Free Delivery Benefit")
                .coupons("INSTA10")
                .cashback(20.0)
                .stockStatus("IN_STOCK")
                .productUrl("https://www.swiggy.com/instamart/search?custom_back=true&query=" + query.replaceAll("\\s+", "+"))
                .platformLogo("https://logo.clearbit.com/swiggy.com")
                .build();
    }
}

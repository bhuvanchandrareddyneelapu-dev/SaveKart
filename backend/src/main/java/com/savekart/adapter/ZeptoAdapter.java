package com.savekart.adapter;

import org.springframework.stereotype.Component;

@Component
public class ZeptoAdapter implements PlatformAdapter {

    @Override
    public String getPlatformName() {
        return "Zepto";
    }

    @Override
    public PlatformPriceFetchResult fetchPrice(String query, String sku) {
        double basePrice = Math.abs(query.hashCode() % 1000) + 150.0;
        double currentPrice = Math.round(basePrice * 0.92);
        double mrp = Math.round(basePrice * 1.20);
        int discount = (int) Math.round(((mrp - currentPrice) / mrp) * 100);

        return PlatformPriceFetchResult.builder()
                .platformName(getPlatformName())
                .currentPrice(currentPrice)
                .mrp(mrp)
                .discountPercentage(discount)
                .deliveryCharge(currentPrice > 199 ? 0.0 : 15.0)
                .deliveryTime("10 mins")
                .rating(4.8)
                .offers("Zepto Pass Super Savings: Free Delivery")
                .coupons("ZEPTOKART")
                .cashback(20.0)
                .stockStatus("IN_STOCK")
                .productUrl("https://www.zeptonow.com/search?q=" + query.replaceAll("\\s+", "+"))
                .platformLogo("https://logo.clearbit.com/zeptonow.com")
                .build();
    }
}

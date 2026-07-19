package com.savekart.adapter;

import org.springframework.stereotype.Component;

@Component
public class JioMartAdapter implements PlatformAdapter {

    @Override
    public String getPlatformName() {
        return "JioMart";
    }

    @Override
    public PlatformPriceFetchResult fetchPrice(String query, String sku) {
        double basePrice = Math.abs(query.hashCode() % 1000) + 150.0;
        double currentPrice = Math.round(basePrice * 0.87);
        double mrp = Math.round(basePrice * 1.20);
        int discount = (int) Math.round(((mrp - currentPrice) / mrp) * 100);

        return PlatformPriceFetchResult.builder()
                .platformName(getPlatformName())
                .currentPrice(currentPrice)
                .mrp(mrp)
                .discountPercentage(discount)
                .deliveryCharge(0.0) // Free Delivery
                .deliveryTime("1-2 Days")
                .rating(4.4)
                .offers("JioMart Maha Cashback Offer")
                .coupons("JIOMART50")
                .cashback(30.0)
                .stockStatus("IN_STOCK")
                .productUrl("https://www.jiomart.com/search/" + query.replaceAll("\\s+", "%20"))
                .platformLogo("https://logo.clearbit.com/jiomart.com")
                .build();
    }
}

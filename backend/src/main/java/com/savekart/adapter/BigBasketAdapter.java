package com.savekart.adapter;

import org.springframework.stereotype.Component;

@Component
public class BigBasketAdapter implements PlatformAdapter {

    @Override
    public String getPlatformName() {
        return "BigBasket";
    }

    @Override
    public PlatformPriceFetchResult fetchPrice(String query, String sku) {
        double basePrice = Math.abs(query.hashCode() % 1000) + 150.0;
        double currentPrice = Math.round(basePrice * 0.89);
        double mrp = Math.round(basePrice * 1.20);
        int discount = (int) Math.round(((mrp - currentPrice) / mrp) * 100);

        return PlatformPriceFetchResult.builder()
                .platformName(getPlatformName())
                .currentPrice(currentPrice)
                .mrp(mrp)
                .discountPercentage(discount)
                .deliveryCharge(currentPrice > 499 ? 0.0 : 30.0)
                .deliveryTime("Same Day")
                .rating(4.5)
                .offers("bbstar Special: Extra 5% NeuCoins")
                .coupons("BIGSAVER")
                .cashback(25.0)
                .stockStatus("IN_STOCK")
                .productUrl("https://www.bigbasket.com/ps/?q=" + query.replaceAll("\\s+", "+"))
                .platformLogo("https://logo.clearbit.com/bigbasket.com")
                .build();
    }
}

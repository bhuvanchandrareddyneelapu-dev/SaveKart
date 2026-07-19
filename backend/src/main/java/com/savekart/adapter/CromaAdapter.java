package com.savekart.adapter;

import org.springframework.stereotype.Component;

@Component
public class CromaAdapter implements PlatformAdapter {

    @Override
    public String getPlatformName() {
        return "Croma";
    }

    @Override
    public PlatformPriceFetchResult fetchPrice(String query, String sku) {
        double basePrice = Math.abs(query.hashCode() % 1000) + 150.0;
        double currentPrice = Math.round(basePrice * 0.90);
        double mrp = Math.round(basePrice * 1.20);
        int discount = (int) Math.round(((mrp - currentPrice) / mrp) * 100);

        return PlatformPriceFetchResult.builder()
                .platformName(getPlatformName())
                .currentPrice(currentPrice)
                .mrp(mrp)
                .discountPercentage(discount)
                .deliveryCharge(0.0)
                .deliveryTime("Same Day Express")
                .rating(4.6)
                .offers("Tata Pay ICICI 10% Instant Discount")
                .coupons("CROMA100")
                .cashback(100.0)
                .stockStatus("IN_STOCK")
                .productUrl("https://www.croma.com/searchB?q=" + query.replaceAll("\\s+", "%20"))
                .platformLogo("https://logo.clearbit.com/croma.com")
                .build();
    }
}

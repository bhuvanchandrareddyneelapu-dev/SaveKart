package com.savekart.adapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformPriceFetchResult {
    private String platformName;
    private Double currentPrice;
    private Double mrp;
    private Integer discountPercentage;
    private Double deliveryCharge;
    private String deliveryTime;
    private Double rating;
    private String offers;
    private String coupons;
    private Double cashback;
    private String stockStatus;
    private String productUrl;
    private String platformLogo;
}

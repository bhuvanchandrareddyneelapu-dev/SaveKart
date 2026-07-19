package com.savekart.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "platform_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String platformName; // Amazon, Flipkart, Zepto, Blinkit, Swiggy Instamart, BigBasket, Dmart Ready, JioMart, Reliance Fresh, Croma, Vijay Sales, Tata Neu, Myntra, Ajio, Meesho

    @Column(nullable = false)
    private Double currentPrice;

    private Double mrp;

    private Integer discountPercentage;

    private Double deliveryCharge; // 0.0 for FREE

    private String deliveryTime; // e.g. "10 mins", "Same Day", "2 Days"

    private Double rating;

    private String offers; // Bank discount or promo text

    private String coupons; // Coupon codes e.g. "SAVE100"

    private Double cashback; // Cashback amount in ₹

    private String stockStatus; // "IN_STOCK", "OUT_OF_STOCK"

    private String productUrl;

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}

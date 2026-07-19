package com.savekart.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String sku;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double mrp;

    private String weight;

    private String mainImage;

    @Column(length = 2000)
    private String additionalImages; // Comma separated image paths

    @Column(length = 1000)
    private String variants; // Comma separated variants e.g. "128GB, 256GB, 512GB"

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Builder.Default
    private Double rating = 4.5;

    @Builder.Default
    private Integer reviewCount = 120;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

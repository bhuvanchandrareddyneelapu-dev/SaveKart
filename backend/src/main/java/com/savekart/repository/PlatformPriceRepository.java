package com.savekart.repository;

import com.savekart.model.PlatformPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PlatformPriceRepository extends JpaRepository<PlatformPrice, Long> {
    List<PlatformPrice> findByProductId(Long productId);
    List<PlatformPrice> findByProductIdOrderByCurrentPriceAsc(Long productId);
    Optional<PlatformPrice> findByProductIdAndPlatformNameIgnoreCase(Long productId, String platformName);
}

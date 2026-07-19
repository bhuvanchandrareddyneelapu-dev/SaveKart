package com.savekart.repository;

import com.savekart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserIdAndProductIdAndSelectedPlatform(Long userId, Long productId, String platform);
    void deleteByUserId(Long userId);
}

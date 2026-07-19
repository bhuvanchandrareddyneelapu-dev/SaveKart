package com.savekart.repository;

import com.savekart.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findByProductIdOrderByRecordedAtAsc(Long productId);
}

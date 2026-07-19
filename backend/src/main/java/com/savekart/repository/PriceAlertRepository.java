package com.savekart.repository;

import com.savekart.model.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {
    List<PriceAlert> findByUserId(Long userId);
}

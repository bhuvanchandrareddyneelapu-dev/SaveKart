package com.savekart.repository;

import com.savekart.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category", "brand"})
    List<Product> findByCategorySlugIgnoreCase(String categorySlug);

    @EntityGraph(attributePaths = {"category", "brand"})
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchProducts(@Param("query") String query);

    @EntityGraph(attributePaths = {"category", "brand"})
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> findTop5ByNameContainingIgnoreCase(@Param("query") String query);

    @EntityGraph(attributePaths = {"category", "brand"})
    List<Product> findTop8ByOrderByRatingDesc();
}

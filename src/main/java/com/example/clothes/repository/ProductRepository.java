package com.example.clothes.repository;

import com.example.clothes.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);

    Optional<Product> findByProductName(String name);

    List<Product> findTop10ByProductNameContainingIgnoreCase(String keyword);

    @Query("""
                SELECT DISTINCT p FROM Product p
                LEFT JOIN p.variants v
                WHERE (:name IS NULL OR p.productName LIKE %:name%)
                  AND (:categoryId IS NULL OR p.categories.id = :categoryId)
                  AND (:sex IS NULL OR p.sex = :sex)
                  AND (:brand IS NULL OR p.brand.id = :brand)
                  AND (:size IS NULL OR v.size = :size)
                  AND (:minPrice IS NULL OR p.price >= :minPrice)
                  AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """)
    Page<Product> search(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("sex") String sex,
            @Param("brand") Long brand,
            @Param("size") String size,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );


    @Query("SELECT SUM(i.quantity) FROM Product p JOIN p.variants i where p.categories.id = :categoryId ")
    Long quantityPerProductByCategoryId(@Param("categoryId") Long categoryId);
}

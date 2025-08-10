package com.example.clothes.repository;

import com.example.clothes.model.Product;
import com.example.clothes.model.Variant;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VariantRepository extends JpaRepository<Variant, Long> {
    Optional<Variant> findByProductAndColorAndSize(Product product, String color, String size);

    List<Variant> findAllByProduct(Product product);

    @Query("SELECT SUM(i.quantity) FROM Variant i WHERE i.product.id = :productId")
    Long sumQuantityByProduct(@Param("productId") Long productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Variant i WHERE i.id in :ids")
    void deleteByIds(@Param("ids") List<Long> ids);

    @Transactional
    @Modifying
    @Query("DELETE  FROM  Variant  i WHERE  i.product.id in :ids")
    void deleteByProduct(@Param("ids") List<Long> ids);

}

package com.example.clothes.repository;

import com.example.clothes.model.VariantImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VariantImageRepository extends JpaRepository<VariantImage, Long> {
    List<VariantImage> findByVariantId(Long variantId);
    List<VariantImage> findByVariantIdIn(List<Long> variantIds);
    @Modifying
    @Query("DELETE FROM VariantImage v WHERE v.variant.id IN :ids")
    void deleteByVariant(@Param("ids") List<Long> variantIds);


}

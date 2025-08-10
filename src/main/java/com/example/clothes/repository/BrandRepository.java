package com.example.clothes.repository;

import com.example.clothes.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query("SELECT b FROM Brand b where (:name is null or b.name = :name)")
    Page<Brand> findByName(@Param("name") String name, Pageable pageable);
    Brand findByName(String name);
}

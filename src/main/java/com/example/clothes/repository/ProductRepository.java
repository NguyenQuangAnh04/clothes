package com.example.clothes.repository;

import com.example.clothes.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);
    Optional<Product> findByProductName(String name);

    List<Product> findTop10ByProductNameContainingIgnoreCase(String keyword);

}

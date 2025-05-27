package com.example.clothes.repository;

import com.example.clothes.model.Inventory;
import com.example.clothes.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct(Product product);
    @Query("SELECT i.quantity from Inventory i where i.product = :productId")
    Integer findByQuantityAndProduct(@Param("productId") Product productId);
    Optional<Inventory> findByProductAndColorAndSize(Product product, String color, String size);
    List<Inventory> findAllByProduct(Product product);
}

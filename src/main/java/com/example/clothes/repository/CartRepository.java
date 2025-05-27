package com.example.clothes.repository;

import com.example.clothes.model.Cart;
import com.example.clothes.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
    @Modifying
    @Transactional
    @Query("DELETE from Cart c where c.id = :id")
    void deleteById(@Param("id") Long id);
}

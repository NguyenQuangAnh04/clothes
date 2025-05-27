package com.example.clothes.repository;

import com.example.clothes.model.Cart;
import com.example.clothes.model.CartItem;
import com.example.clothes.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductAndSizeAndColor(Cart cart, Product product, String size, String color);
    List<CartItem> findByCart(Cart cart);
    Boolean existsByCart(Cart cart);
    List<CartItem> findByIdIn(List<Long> orderId);
}

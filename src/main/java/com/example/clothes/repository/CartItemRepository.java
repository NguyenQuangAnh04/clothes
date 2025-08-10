package com.example.clothes.repository;

import com.example.clothes.model.Cart;
import com.example.clothes.model.CartItem;
import com.example.clothes.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductAndSizeAndColor(Cart cart, Product product, String size, String color);
    List<CartItem> findByCart(Cart cart);

    Boolean existsByCart(Cart cart);
    @Query("SELECT SUM(c.price * c.quantity) FROM CartItem c WHERE c.cart.id = :cartId")
    BigDecimal sumPricesByCart(@Param("cartId") Long cartId);
    @Modifying
    @Query("DELETE FROM CartItem c where c.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);
}

package com.example.clothes.service;

import com.example.clothes.dto.CartDTO;
import com.example.clothes.dto.CartItemDTO;
import com.example.clothes.model.Cart;

import java.util.List;

public interface ICartService {
    CartDTO createCart(CartDTO cartDTO);
    CartItemDTO updateQuantity(CartItemDTO cartItemDTO);

    CartDTO findByUser();
    void deleteCartItem(Long id);

    void increasedQuantity(Long id);
    void decreasedQuantity(Long id);
}

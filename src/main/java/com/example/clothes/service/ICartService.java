package com.example.clothes.service;

import com.example.clothes.dto.CartDTO;
import com.example.clothes.dto.CartItemDTO;
import com.example.clothes.model.Cart;

import java.util.List;

public interface ICartService {
    CartDTO createCart(CartDTO cartDTO, Long userId);
    CartItemDTO updateQuantity(CartItemDTO cartItemDTO);

    CartDTO findByUser(Long userId);
    void deleteCartItem(Long id);
}

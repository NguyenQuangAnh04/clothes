package com.example.clothes.mapper;

import com.example.clothes.dto.CartDTO;
import com.example.clothes.dto.CartItemDTO;
import com.example.clothes.model.Cart;
import com.example.clothes.model.CartItem;
import com.example.clothes.repository.OrderItemRepository;
import com.example.clothes.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CartMapperDTO {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;

    public CartItemDTO mapCartItemToDTO(CartItem cartItem) {
        return CartItemDTO.builder()
                .id(cartItem.getId())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .size(cartItem.getSize())
                .color(cartItem.getColor())
                .image_url(cartItem.getProduct().getImage_url())
                .productName(cartItem.getProduct().getProductName())
                .build();
    }
}

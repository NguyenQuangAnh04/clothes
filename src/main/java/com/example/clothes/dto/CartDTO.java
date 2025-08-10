package com.example.clothes.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private List<CartItemDTO> carts;
    private BigDecimal totalMoney;

}

package com.example.clothes.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double totalMoney;
    private Double price;
    private String image_url;
    private String size;
    private String color;
    private Integer maxQuantity;
}

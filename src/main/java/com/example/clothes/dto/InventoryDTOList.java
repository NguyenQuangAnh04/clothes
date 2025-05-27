package com.example.clothes.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class InventoryDTOList {
    private Long id;
    private String color;
    private String size;
    private Integer quantity;
    private String image_url;
}

package com.example.clothes.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class OrderItemDTO {
    private Long id;
    private String productName;
    private String image_url;
    private Long productId;
    private Integer quantity;
    private String note;
    private LocalDateTime create_at;
    private Double totalAmount;
    private Double price;
    private String size;
    private String color;
}

package com.example.clothes.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String productName;
    private String description;
    private Double price;
    private String slug;
    private LocalDateTime createdAt;
    private String image_url;
    private LocalDateTime updatedAt;
    private String categoryName;
    private Long categoryId;
    private List<InventoryDTOList> dtoList;
}

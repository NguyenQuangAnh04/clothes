package com.example.clothes.dto;

import com.example.clothes.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String productName;
    private String description;
    private Double price;
    private String slug;
    private LocalDateTime createdAt;
    private String image_url;
    private String brand;
    private Long stock;
    private LocalDateTime updatedAt;
    private String categoryName;
    private String sex;
    private Long categoryId;
    private ProductStatus status;
    private List<VariantDTO> variants;
}

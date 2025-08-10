package com.example.clothes.dto;

import com.example.clothes.model.VariantImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantDTO {
    private Long id;
    private String color;
    private String size;
    private Integer quantity;
    private List<VariantImageDTO> images;
}

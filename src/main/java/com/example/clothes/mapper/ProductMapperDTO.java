package com.example.clothes.mapper;

import com.example.clothes.dto.ProductDTO;
import com.example.clothes.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapperDTO {
    public ProductDTO toProductDTO(Product product){
       return ProductDTO.builder()
               .id(product.getId())
               .productName(product.getProductName())
               .sex(product.getSex())
               .slug(product.getSlug())
               .price(product.getPrice())
               .description(product.getDescription())

               .build();

    }
}

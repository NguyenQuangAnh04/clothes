package com.example.clothes.service;

import com.example.clothes.dto.ProductDTO;
import com.example.clothes.model.Product;
import com.example.clothes.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface IProductService {
    Page<ProductDTO> findAll(String name, Long categoryId, String sex, Long brand, String size,
                             BigDecimal minPrice, BigDecimal maxPrice,
                             PageRequest pageRequest);
    Page<ProductDTO> findAllHome(String name, Long categoryId, String sex, Long brand, String size,
                             BigDecimal minPrice, BigDecimal maxPrice,
                             PageRequest pageRequest);
    Map<String, Object> createProduct(ProductDTO productDTO);
    Map<String, Object> updateProduct(ProductDTO productDTO, List<Long> variantIds);
    List<ProductResponse> getSuggestions(String keyword);
    ProductDTO findBySlug(String slug);
    void delete(Long id);

}

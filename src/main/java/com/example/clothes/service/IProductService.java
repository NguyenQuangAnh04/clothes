package com.example.clothes.service;

import com.example.clothes.dto.ProductDTO;
import com.example.clothes.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProductService {
    Page<ProductDTO> findAll(PageRequest pageRequest);
    ProductDTO createOrUpdate(ProductDTO productDTO);
    List<ProductDTO> getSuggestions(String keyword);
    ProductDTO findBySlug(String slug);
    void delete(Long id);
}

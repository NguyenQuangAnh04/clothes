package com.example.clothes.service;

import com.example.clothes.dto.BrandDTO;
import com.example.clothes.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBrandService {
    Page<BrandDTO> findAll(String name, Pageable pageable);
    BrandDTO createBrand(BrandDTO brandDTO);
    BrandDTO updateBrand(BrandDTO brandDTO);
    void deleteBrand(Long id);
}

package com.example.clothes.service;

import com.example.clothes.dto.BrandDTO;
import com.example.clothes.model.Brand;
import com.example.clothes.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BrandService implements IBrandService {
    @Autowired
    private BrandRepository brandRepository;

    @Override
    public Page<BrandDTO> findAll(String name, Pageable pageable) {
       Page<Brand> brands = brandRepository.findByName(name, pageable);
       return brands.map(item-> {
           return BrandDTO.builder()
                   .id(item.getId())
                   .name(item.getName())
                   .build();
       });
    }

    @Override
    public BrandDTO createBrand(BrandDTO brandDTO) {
        Brand brand = brandRepository.findByName(brandDTO.getName());
        if (brand != null) {
            throw new RuntimeException("Name brand is already exists");
        }
        brand = Brand.builder()
                .name(brandDTO.getName())
                .created_at(LocalDateTime.now())
                .build();
        brandRepository.save(brand);
        return brandDTO;
    }

    @Override
    public BrandDTO updateBrand(BrandDTO brandDTO) {
        if (brandDTO == null) {
            throw new RuntimeException("BrandDTO is null");
        }

        Brand brand = brandRepository.findByName(brandDTO.getName());
        if (brand != null) {
            throw new RuntimeException("Brand with this name already exists");
        }
        brand = brandRepository.findById(brandDTO.getId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        brand.setName(brandDTO.getName());
        brand.setUpdated_at(LocalDateTime.now());
        brandRepository.save(brand);
        return brandDTO;
    }

    @Override
    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }

}

package com.example.clothes.service;

import com.example.clothes.dto.CategoryDTO;
import com.example.clothes.model.Categories;
import com.example.clothes.model.Product;
import com.example.clothes.repository.CategoriesRepository;
import com.example.clothes.repository.ProductRepository;
import com.example.clothes.repository.VariantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoriesService implements ICategoriesService {
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VariantRepository variantRepository;

    @Override
    public Categories createCategory(CategoryDTO categoryDTO) {
        Categories categories = categoriesRepository.findByCategoryName(categoryDTO.getCategory_name());
        if (categories != null) {
            throw new RuntimeException("Category đã tồn tại");
        }
        Categories newCategory = new Categories();
        newCategory.setCategoryName(categoryDTO.getCategory_name());
        newCategory.setCreated_at(LocalDateTime.now());
        return categoriesRepository.save(newCategory);
    }

    @Override
    public Page<CategoryDTO> findAll(PageRequest pageRequest) {
        Page<Categories> categoriesPage = categoriesRepository.findAll(pageRequest);
        Page<CategoryDTO> page = categoriesPage.map(categories -> {
            Long count = productRepository.quantityPerProductByCategoryId(categories.getId());
            return CategoryDTO.builder()
                    .totalProduct(count != null ? count : 0)
                    .id(categories.getId())
                    .createdAt(categories.getCreated_at())
                    .updatedAt(categories.getUpdated_at())
                    .category_name(categories.getCategoryName())
                    .build();
        });
        return page;

    }

    @Override
    public Categories updateCategory(CategoryDTO categoryDTO) {
        Categories categories = categoriesRepository.findById(categoryDTO.getId()).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy category"));
        categories.setCategoryName(categoryDTO.getCategory_name());
        categories.setUpdated_at(LocalDateTime.now());
        return categoriesRepository.save(categories);

    }

    @Transactional
    @Override
    public void deleteCategory(Long categoryId) {
        Categories categories = categoriesRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy category"));
        List<Long> allInventory = new ArrayList<>();
        for (Product product : categories.getProducts()) {
            allInventory.add(product.getId());
        }
        variantRepository.deleteByProduct(allInventory);
        categoriesRepository.delete(categories);
    }
}

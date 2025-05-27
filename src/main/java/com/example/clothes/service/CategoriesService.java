package com.example.clothes.service;

import com.example.clothes.dto.CategoryDTO;
import com.example.clothes.model.Categories;
import com.example.clothes.repository.CategoriesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriesService implements ICategoriesService {
    @Autowired
    private CategoriesRepository categoriesRepository;

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
    public List<Categories> findAll() {
       return categoriesRepository.findAll();
    }

    @Override
    public Categories updateCategory(CategoryDTO categoryDTO) {
        Categories categories = categoriesRepository.findById(categoryDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy category"));
        categories.setCategoryName(categoryDTO.getCategory_name());
        categories.setDescription(categoryDTO.getDescription());
        return categoriesRepository.save(categories);

    }

    @Override
    public void deleteCategory(Long categoryId) {
        Categories categories = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy category"));
        categoriesRepository.delete(categories);
    }
}

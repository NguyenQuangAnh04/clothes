package com.example.clothes.service;

import com.example.clothes.dto.CategoryDTO;
import com.example.clothes.model.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ICategoriesService {
    Categories createCategory(CategoryDTO categoryDTO);
    Page<CategoryDTO> findAll(PageRequest pageRequest);

    Categories updateCategory(CategoryDTO categoryDTO);

    void deleteCategory(Long categoryId);
}

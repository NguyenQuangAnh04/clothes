package com.example.clothes.service;

import com.example.clothes.dto.CategoryDTO;
import com.example.clothes.model.Categories;

import java.util.List;

public interface ICategoriesService {
    Categories createCategory(CategoryDTO categoryDTO);
    List<Categories> findAll();

    Categories updateCategory(CategoryDTO categoryDTO);

    void deleteCategory(Long categoryId);
}

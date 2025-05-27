package com.example.clothes.repository;

import com.example.clothes.model.Categories;
import com.example.clothes.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    Categories findByCategoryName(String name);
}

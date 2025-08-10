package com.example.clothes.controller;

import com.example.clothes.dto.CategoryDTO;
import com.example.clothes.model.Categories;
import com.example.clothes.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoriesService categoriesService;

    @PostMapping("/add-category")
    public ResponseEntity<Categories> addCategory(@RequestBody() CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoriesService.createCategory(categoryDTO));
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(name = "page", defaultValue = "0") int page
            , @RequestParam(value = "limit", defaultValue = "5") int limit) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<CategoryDTO> categoryPage = categoriesService.findAll(pageRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("totalPages", categoryPage.getTotalPages());
        response.put("totalItems", categoryPage.getTotalElements());
        response.put("categories", categoryPage.getContent());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        categoriesService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Categories> update(@RequestBody() CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoriesService.updateCategory(categoryDTO));
    }
}

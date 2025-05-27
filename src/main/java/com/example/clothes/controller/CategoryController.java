package com.example.clothes.controller;

import com.example.clothes.dto.CategoryDTO;
import com.example.clothes.model.Categories;
import com.example.clothes.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<Categories>> findAll(){
        return ResponseEntity.ok(categoriesService.findAll());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id){
        categoriesService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Categories> update(@RequestBody() CategoryDTO categoryDTO){
        return ResponseEntity.ok(categoriesService.updateCategory(categoryDTO));
    }
}

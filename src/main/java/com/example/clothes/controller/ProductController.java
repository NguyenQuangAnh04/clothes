package com.example.clothes.controller;

import com.example.clothes.dto.ProductDTO;
import com.example.clothes.model.Product;
import com.example.clothes.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")

@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "5") int limit) {

        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());
        Page<ProductDTO> product = productService.findAll(pageRequest);
        Map<String, Object> map = new HashMap<>();
        map.put("products", product.getContent());
        map.put("totalPages", product.getTotalPages());
        map.put("totalItems", product.getTotalElements());
        map.put("currentPage", product.getNumber());
        return ResponseEntity.ok(map);
    }

    @PostMapping("/add-or-update")
    public ResponseEntity<ProductDTO> createOrUpdate(@RequestBody() ProductDTO productDTO) {
        return ResponseEntity.ok(productService.createOrUpdate(productDTO));
    }
    @GetMapping("/suggestions")
    public ResponseEntity<List<ProductDTO>> getSuggestion(@RequestParam("keywords") String keywords){
        return ResponseEntity.ok(productService.getSuggestions(keywords));
    }
    @GetMapping("/search")
    public ResponseEntity<ProductDTO> searchBySlug(@RequestParam(name = "slug") String slug) {
        return ResponseEntity.ok(productService.findBySlug(slug));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Long id){
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}

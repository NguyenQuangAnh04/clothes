package com.example.clothes.controller;

import com.example.clothes.dto.ProductDTO;
import com.example.clothes.response.ProductResponse;
import com.example.clothes.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "4") int limit,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "sex", required = false) String sex,
            @RequestParam(name = "brand", required = false) Long brand,
            @RequestParam(name = "size", required = false) String size,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice

    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<ProductDTO> product = productService.findAll(name, categoryId, sex, brand, size,minPrice,maxPrice, pageRequest);
        Map<String, Object> map = new HashMap<>();
        map.put("products", product.getContent());
        map.put("totalPages", product.getTotalPages());
        map.put("totalItems", product.getTotalElements());
        return ResponseEntity.ok(map);
    }
    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> findAllHome(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "sex", required = false) String sex,
            @RequestParam(name = "brand", required = false) Long brand,
            @RequestParam(name = "size", required = false) String size,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice

    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<ProductDTO> product = productService.findAll(name, categoryId, sex, brand, size,minPrice,maxPrice, pageRequest);
        Map<String, Object> map = new HashMap<>();
        map.put("products", product.getContent());
        map.put("totalPages", product.getTotalPages());
        map.put("totalItems", product.getTotalElements());
        return ResponseEntity.ok(map);
    }
    @PostMapping("/create-product")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody() ProductDTO productDTO) {
        return ResponseEntity.ok(productService.createProduct(productDTO));
    }

    @PutMapping("/update-product")
    public ResponseEntity<Map<String, Object>> updateProduct(@RequestBody() ProductDTO productDTO
            , @RequestParam(value = "ids", required = false) List<Long> variantIds) {
        return ResponseEntity.ok(productService.updateProduct(productDTO, variantIds));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<ProductResponse>> getSuggestion(@RequestParam("keywords") String keywords) {
        return ResponseEntity.ok(productService.getSuggestions(keywords));
    }

    @GetMapping("/search")
    public ResponseEntity<ProductDTO> searchBySlug(@RequestParam(name = "slug") String slug) {
        return ResponseEntity.ok(productService.findBySlug(slug));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Long id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}

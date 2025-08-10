package com.example.clothes.controller;

import com.example.clothes.dto.BrandDTO;
import com.example.clothes.model.Brand;
import com.example.clothes.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(name = "name", required = false) String name,
                                       @RequestParam(name = "page", defaultValue = "0") int page,
                                       @RequestParam(name = "limit", defaultValue = "5") int limit) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<BrandDTO> brands = brandService.findAll(name, pageRequest);
        Map<String, Object> map = new HashMap<>();
        map.put("brands", brands.getContent());
        map.put("totalItems", brands.getTotalElements());
        map.put("totalPages", brands.getTotalPages());
        return ResponseEntity.ok(map);
    }

    // Tạo brand mới
    @PostMapping("")
    public ResponseEntity<BrandDTO> createBrand(@RequestBody BrandDTO brandDTO) {
        BrandDTO createdBrand = brandService.createBrand(brandDTO);
        return ResponseEntity.ok(createdBrand);
    }

    // Cập nhật brand
    @PutMapping("/{id}")
    public ResponseEntity<BrandDTO> updateBrand(@PathVariable Long id, @RequestBody BrandDTO brandDTO) {
        // Đảm bảo id trong URL trùng với id trong DTO
        if (!id.equals(brandDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }

        BrandDTO updatedBrand = brandService.updateBrand(brandDTO);
        return ResponseEntity.ok(updatedBrand);
    }

    // Xóa brand
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}

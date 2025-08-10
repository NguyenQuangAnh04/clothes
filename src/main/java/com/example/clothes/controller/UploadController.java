package com.example.clothes.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.clothes.model.Variant;
import com.example.clothes.model.VariantImage;
import com.example.clothes.repository.VariantImageRepository;
import com.example.clothes.repository.VariantRepository;
import com.example.clothes.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private VariantImageRepository variantImageRepository;

    @PostMapping("/{id}")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, @PathVariable(name = "id") Long id) {
        try {
            String url = cloudinaryService.uploadFile(file, id);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/upload-multiple-variant")
    public ResponseEntity<?> uploadMultipleVariant(@RequestParam MultiValueMap<String, MultipartFile> files,
                                                   @RequestParam("variantIds") List<Long> variantIds) throws IOException {
        List<VariantImage> savedImages = new ArrayList<>();
        for (int i = 0; i < variantIds.size(); i++) {
            Variant variant = variantRepository.findById(variantIds.get(i)).orElseThrow(() -> new RuntimeException("Variant not found"));
//            if(variantIds.size() == 1) i++;
            String key = "files[" + i + "]";
            List<MultipartFile> filesForVariant = files.get(key);
            if (filesForVariant == null || filesForVariant.isEmpty()) {
                System.out.println("No files for key: " + key);
                continue;
            }
            for (MultipartFile fileItem : filesForVariant) {
                VariantImage variantImage = new VariantImage();
                Map<?, ?> map = cloudinary.uploader().upload(fileItem.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                variantImage.setImage_url(map.get("secure_url").toString());
                variantImage.setVariant(variant);
                variantImage.setCreated_at(LocalDateTime.now());
                savedImages.add(variantImageRepository.save(variantImage));
            }
        }
        return ResponseEntity.ok(savedImages);
    }
}

package com.example.clothes.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.clothes.model.Product;
import com.example.clothes.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private ProductRepository productRepository;
    public String uploadFile(MultipartFile file, Long productId) throws IOException{
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Không tìm thấy id sản phẩm"));
        Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));
        product.setImage_url(uploadResult.get("secure_url").toString());
        productRepository.save(product);
        return uploadResult.get("secure_url").toString();
    }
}

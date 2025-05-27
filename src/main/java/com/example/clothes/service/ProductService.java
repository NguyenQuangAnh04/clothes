package com.example.clothes.service;

import com.example.clothes.dto.InventoryDTOList;
import com.example.clothes.dto.ProductDTO;
import com.example.clothes.model.Categories;
import com.example.clothes.model.Inventory;
import com.example.clothes.model.Product;
import com.example.clothes.repository.CategoriesRepository;
import com.example.clothes.repository.InventoryRepository;
import com.example.clothes.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public Page<ProductDTO> findAll(PageRequest pageRequest) {
        Page<Product> productPage = productRepository.findAll(pageRequest);
        List<ProductDTO> dtos = productPage.stream().map(item -> {
            ProductDTO dto = new ProductDTO();
            dto.setId(item.getId());
            dto.setProductName(item.getProductName());
            dto.setImage_url(item.getImage_url());
            dto.setCategoryName(item.getCategories().getCategoryName());
            dto.setPrice(item.getPrice());
            List<InventoryDTOList> inventoryDTOs = item.getInventories().stream().map(inventory -> {
                InventoryDTOList invDto = new InventoryDTOList();
                invDto.setId(inventory.getId());
                invDto.setSize(inventory.getSize());
                invDto.setColor(inventory.getColor());
                invDto.setImage_url(inventory.getImage_url());
                invDto.setQuantity(inventory.getQuantity());
                return invDto;
            }).collect(Collectors.toList());
            dto.setDtoList(inventoryDTOs);
            return dto;
        }).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageRequest, productPage.getTotalElements());

    }

    @Override
    public ProductDTO createOrUpdate(ProductDTO productDTO) {
        Categories categories = categoriesRepository.findByCategoryName(productDTO.getCategoryName());
        if (categories == null) {
            throw new RuntimeException("Không tìm thấy category");
        }
        if (productDTO.getId() != null) {
            Product product = productRepository.findById(productDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy id"));
            if (!product.getProductName().equals(productDTO.getProductName())) {
                Optional<Product> existName = productRepository.findByProductName(productDTO.getProductName());
                if (existName.isPresent()) throw new RuntimeException("Tên sản phẩm đã tồn tại!");
            }
            product.setProductName(productDTO.getProductName());
            product.setPrice(productDTO.getPrice());
            product.setUpdated_at(LocalDateTime.now());
            productRepository.save(product);
            for (InventoryDTOList itemDTO : productDTO.getDtoList()) {
                if (itemDTO.getId() != null) {
                    Inventory inventory = inventoryRepository.findById(itemDTO.getId()).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
                    inventory.setQuantity(itemDTO.getQuantity());
                    inventory.setSize(itemDTO.getSize());
                    inventory.setColor(itemDTO.getColor());
                    inventory.setLast_updated(LocalDateTime.now());
                    inventoryRepository.save(inventory);
                } else {
                    Inventory inventory = new Inventory();
                    inventory.setQuantity(itemDTO.getQuantity());
                    inventory.setSize(itemDTO.getSize());
                    inventory.setProduct(product);
                    inventory.setColor(itemDTO.getColor());
                    inventory.setLast_updated(LocalDateTime.now());
                    inventoryRepository.save(inventory);
                }
            }
        } else {
            if (productRepository.findByProductName(productDTO.getProductName()).isPresent()) {
                throw new RuntimeException("Tên sản phẩm đã tồn tại!");
            }
            Product newProduct = new Product();
            newProduct.setProductName(productDTO.getProductName());
            newProduct.setCategories(categories);
            newProduct.setPrice(productDTO.getPrice());
            String slug = toSlug(productDTO.getProductName());
            newProduct.setSlug(slug);
            newProduct.setCreate_at(LocalDateTime.now());
            productRepository.save(newProduct);
            for (InventoryDTOList itemDTO : productDTO.getDtoList()) {
                Inventory inventory = new Inventory();
                inventory.setProduct(newProduct);
                inventory.setQuantity(itemDTO.getQuantity());
                inventory.setColor(itemDTO.getColor());
                inventory.setSize(itemDTO.getSize());
                inventory.setLast_updated(LocalDateTime.now());
                inventoryRepository.save(inventory);
            }
        }
        return productDTO;
    }

    @Override
    public List<ProductDTO> getSuggestions(String keyword) {
        List<Product> products = productRepository.findTop10ByProductNameContainingIgnoreCase(keyword);
        List<ProductDTO> productDTOS = new ArrayList<>();
        for (Product item : products) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductName(item.getProductName());
            productDTO.setSlug(item.getSlug());
            productDTO.setPrice(item.getPrice());
            productDTO.setImage_url(item.getImage_url());
            productDTOS.add(productDTO);
        }
        return productDTOS;
    }

    @Override
    public ProductDTO findBySlug(String slug) {
        Optional<Product> findByName = productRepository.findBySlug(slug);
        if (!findByName.isPresent()) {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(findByName.get().getId());
        productDTO.setProductName(findByName.get().getProductName());
        productDTO.setImage_url(findByName.get().getImage_url());
        productDTO.setCategoryName(findByName.get().getCategories().getCategoryName());
        productDTO.setPrice(findByName.get().getPrice());
        List<Inventory> inventories = inventoryRepository.findAllByProduct(findByName.get());
        List<InventoryDTOList> list = new ArrayList<>();
        for (Inventory inventory : inventories) {
            InventoryDTOList inventoryDTOList = new InventoryDTOList();
            inventoryDTOList.setId(inventory.getId());
            inventoryDTOList.setSize(inventory.getSize());
            inventoryDTOList.setQuantity(inventory.getQuantity());
            inventoryDTOList.setColor(inventory.getColor());
            inventoryDTOList.setImage_url(inventory.getImage_url());
            list.add(inventoryDTOList);
        }
        productDTO.setDtoList(list);
        return productDTO;
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy id sản phẩm"));
        productRepository.delete(product);
    }

    public String removeAccent(String s) {
        // B1: Chuẩn hóa chuỗi sang dạng NFD (mỗi ký tự tách riêng dấu thanh)
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
        // B2: Regex xóa các ký tự thuộc block "Combining Diacritical Marks" (dấu sắc, huyền, mũ...)
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        // B3: Xử lý riêng cho ký tự 'đ' và 'Đ' vì nó không tách được dấu
        return pattern.matcher(normalized).replaceAll("").replaceAll("d", "d").replaceAll("Đ", "D");
    }

    public String toSlug(String input) {
        String noAccent = removeAccent(input);
        return noAccent.toLowerCase()
                .trim()
                .replaceAll("\\s+", "-")             // đổi khoảng trắng thành -
                .replaceAll("[^a-z0-9\\-]", "")      // xóa ký tự đặc biệt
                .replaceAll("-{2,}", "-")            // gộp nhiều dấu - liên tiếp
                .replaceAll("^-|-$", "");            // xóa - ở đầu/cuối nếu có
    }

}

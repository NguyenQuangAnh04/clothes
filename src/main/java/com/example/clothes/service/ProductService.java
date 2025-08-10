package com.example.clothes.service;

import com.example.clothes.dto.ProductDTO;
import com.example.clothes.dto.VariantDTO;
import com.example.clothes.dto.VariantImageDTO;
import com.example.clothes.model.*;
import com.example.clothes.repository.*;
import com.example.clothes.response.ProductResponse;
import com.github.slugify.Slugify;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService implements IProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private VariantRepository variantRepository;
    ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    @Autowired
    private VariantImageRepository variantImageRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private BrandRepository brandRepository;

    @Override
    public Page<ProductDTO> findAll(String name, Long categoryId, String sex, Long brand, String size, BigDecimal minPrice, BigDecimal maxPrice, PageRequest pageRequest) {
        Page<Product> productPage = productRepository.search(name, categoryId, sex, brand, size, minPrice, maxPrice, pageRequest);
        Page<ProductDTO> dtos = productPage.map(item -> {
            ProductDTO dto = new ProductDTO();
            Long total = variantRepository.sumQuantityByProduct(item.getId());
            dto.setStock(total);
            dto.setId(item.getId());
            dto.setProductName(item.getProductName());
            dto.setSlug(item.getSlug());
            dto.setDescription(item.getDescription());
            dto.setCreatedAt(item.getCreate_at());
            dto.setCategoryId(item.getCategories().getId());
            dto.setSex(item.getSex());
            dto.setBrand(item.getBrand().getName());
            dto.setImage_url(item.getImage_url());
            dto.setCategoryName(item.getCategories().getCategoryName());
            dto.setPrice(item.getPrice());
            List<VariantDTO> variants = item.getVariants().stream().map(variant -> {
                VariantDTO variantDTO = new VariantDTO();
                variantDTO.setId(variant.getId());
                variantDTO.setSize(variant.getSize());
                variantDTO.setColor(variant.getColor());
                List<VariantImage> variantImages = variantImageRepository.findByVariantId(variant.getId());
                List<VariantImageDTO> variantImageDTOS = new ArrayList<>();
                for (VariantImage variantImage : variantImages) {
                    VariantImageDTO variantImageDTO = new VariantImageDTO();
                    variantImageDTO.setId(variantImage.getId());
                    variantImageDTO.setImage_url(variantImage.getImage_url());
                    variantImageDTOS.add(variantImageDTO);
                }
                variantDTO.setImages(variantImageDTOS);
                variantDTO.setQuantity(variant.getQuantity());
                return variantDTO;
            }).collect(Collectors.toList());
            dto.setVariants(variants);
            return dto;
        });
        return dtos;

    }

    @Override
    public Page<ProductDTO> findAllHome(String name, Long categoryId, String sex, Long brand, String size, BigDecimal minPrice, BigDecimal maxPrice, PageRequest pageRequest) {
        Page<Product> productPage = productRepository.search(name, categoryId, sex, brand, size, minPrice, maxPrice, pageRequest);
        Page<ProductDTO> dtos = productPage.map(item -> {
            ProductDTO dto = new ProductDTO();
            Long total = variantRepository.sumQuantityByProduct(item.getId());
            dto.setStock(total);
            dto.setId(item.getId());
            dto.setProductName(item.getProductName());
            dto.setSlug(item.getSlug());
            dto.setDescription(item.getDescription());
            dto.setCreatedAt(item.getCreate_at());
            dto.setCategoryId(item.getCategories().getId());
            dto.setSex(item.getSex());
            dto.setBrand(item.getBrand().getName());
            dto.setImage_url(item.getImage_url());
            dto.setCategoryName(item.getCategories().getCategoryName());
            dto.setPrice(item.getPrice());
            List<VariantDTO> variants = item.getVariants().stream().map(variant -> {
                VariantDTO variantDTO = new VariantDTO();
                variantDTO.setId(variant.getId());
                variantDTO.setSize(variant.getSize());
                variantDTO.setColor(variant.getColor());
                List<VariantImage> variantImages = variantImageRepository.findByVariantId(variant.getId());
                List<VariantImageDTO> variantImageDTOS = new ArrayList<>();
                for (VariantImage variantImage : variantImages) {
                    VariantImageDTO variantImageDTO = new VariantImageDTO();
                    variantImageDTO.setId(variantImage.getId());
                    variantImageDTO.setImage_url(variantImage.getImage_url());
                    variantImageDTOS.add(variantImageDTO);
                }
                variantDTO.setImages(variantImageDTOS);
                variantDTO.setQuantity(variant.getQuantity());
                return variantDTO;
            }).collect(Collectors.toList());
            dto.setVariants(variants);
            return dto;
        });
        return dtos;
    }

    @Override
    public Map<String, Object> createProduct(ProductDTO productDTO) {
        Optional<Categories> categories = categoriesRepository.findById(productDTO.getCategoryId());
        if (categories.isEmpty()) {
            throw new EntityNotFoundException(productDTO.getCategoryName());
        }
        if (productRepository.findByProductName(productDTO.getProductName()).isPresent()) {
            throw new RuntimeException("Tên sản phẩm đã tồn tại!");
        }
        Slugify slugify = new Slugify();
        String slug = slugify.slugify(productDTO.getProductName());
        Brand brand = brandRepository.findByName(productDTO.getBrand());
        if (brand == null) {
            throw new EntityNotFoundException(productDTO.getBrand());
        }
        Product product = Product.builder().productName(productDTO.getProductName())
                .categories(categories.get())
                .brand(brand)
                .price(productDTO.getPrice())
                .sex(productDTO.getSex())
                .create_at(LocalDateTime.now())
                .description(productDTO.getDescription())
                .create_at(LocalDateTime.now())
                .slug(slug).build();
        productRepository.save(product);
        List<Long> variantIds = new ArrayList<>();

        if (productDTO.getVariants() != null) {
            for (VariantDTO itemDTO : productDTO.getVariants()) {
                Variant variant = Variant.builder().color(itemDTO.getColor()).product(product).quantity(itemDTO.getQuantity()).size(itemDTO.getSize()).created_at(LocalDateTime.now()).build();
                variantRepository.save(variant);
                variantIds.add(variant.getId());
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("productId", product.getId());
        if (variantIds.size() > 0) {
            map.put("variantIds", variantIds);
        }
        return map;
    }

    @Transactional
    @Override
    public Map<String, Object> updateProduct(ProductDTO productDTO, List<Long> variantIds) {
        if (productDTO.getId() == null) {
            throw new IllegalArgumentException("Id not null");
        }
        Product product = productRepository.findById(productDTO.getId()).orElseThrow(EntityNotFoundException::new);
        if (!product.getProductName().equals(productDTO.getProductName())) {
            Optional<Product> existName = productRepository.findByProductName(productDTO.getProductName());
            if (existName.isPresent()) {
                throw new RuntimeException("Tên sản phẩm đã tồn tại!");
            }
        }
        Brand brand = brandRepository.findByName(productDTO.getBrand());
        if (brand == null) {
            throw new EntityNotFoundException(productDTO.getBrand());
        }
        Categories categories = categoriesRepository.findById(productDTO.getCategoryId()).orElseThrow(EntityNotFoundException::new);
        product.setProductName(productDTO.getProductName());
        Slugify slugify = new Slugify();
        String slug = slugify.slugify(productDTO.getProductName());
        product.setSlug(slug);
        product.setBrand(brand);
        product.setDescription(productDTO.getDescription());
        product.setSex(productDTO.getSex());
        product.setPrice(productDTO.getPrice());
        if (productDTO.getImage_url() == null || productDTO.getImage_url().isEmpty()) {
            product.setImage_url("");
        }
        product.setCategories(categories);
        product.setUpdated_at(LocalDateTime.now());
        productRepository.save(product);
        List<Long> variantId = new ArrayList<>();

        for (VariantDTO itemDTO : productDTO.getVariants()) {
            if (itemDTO.getId() == null) {
                Variant newInventory = Variant.builder().product(product).created_at(LocalDateTime.now())
                        .color(itemDTO.getColor())
                        .quantity(itemDTO.getQuantity())
                        .size(itemDTO.getSize())
                        .build();
                variantRepository.save(newInventory);
                variantId.add(newInventory.getId());
            } else {
                Optional<Variant> variant = variantRepository.findById(itemDTO.getId());
                variant.get().setSize(itemDTO.getSize());
                variant.get().setQuantity(itemDTO.getQuantity());
                variant.get().setColor(itemDTO.getColor());
                boolean hasNewImages = false;
                for (VariantImageDTO variantImageDTO : itemDTO.getImages()) {
                    if (variantImageDTO.getImage_url() == null) {
                        VariantImage variantImage = variantImageRepository.findById(variantImageDTO.getId()).orElseThrow(EntityNotFoundException::new);
                        variantImageRepository.delete(variantImage);
                    } else if (variantImageDTO.getId() == null) {
                        hasNewImages = true;
                    }
                }
                variant.get().setLast_updated(LocalDateTime.now());
                variantRepository.save(variant.get());
                if (hasNewImages) {
                    variantId.add(variant.get().getId());
                }
            }
        }
        if (variantIds != null && !variantIds.isEmpty()) {
            List<VariantImage> variantImages = variantImageRepository.findByVariantIdIn(variantIds);
            if (!variantImages.isEmpty()) {
                variantImageRepository.deleteAll(variantImages);
                variantImageRepository.flush();
            };
            variantRepository.deleteByIds(variantIds);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("productId", product.getId());
        map.put("variantId", variantId);
        log.info(map.toString());

        return map;
    }


    @Override
    public List<ProductResponse> getSuggestions(String keyword) {
        List<Product> products = productRepository.findTop10ByProductNameContainingIgnoreCase(keyword);
       return products.stream().map(item -> {
           return ProductResponse.builder()
                   .name(item.getProductName())
                   .image_url(item.getImage_url())
                   .slug(item.getSlug())
                   .price(item.getPrice())
                   .build();
       }).collect(Collectors.toList());
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
        productDTO.setStock(variantRepository.sumQuantityByProduct(findByName.get().getId()));
        productDTO.setPrice(findByName.get().getPrice());
        List<Variant> variants = variantRepository.findAllByProduct(findByName.get());
        List<VariantDTO> list = new ArrayList<>();
        for (Variant variant : variants) {
            VariantDTO variantDTO = new VariantDTO();
            variantDTO.setId(variant.getId());
            variantDTO.setSize(variant.getSize());
            variantDTO.setQuantity(variant.getQuantity());
            variantDTO.setColor(variant.getColor());
            List<VariantImageDTO> variantImageDTOS = new ArrayList<>();
            List<VariantImage> variantImages = variantImageRepository.findByVariantId(variant.getId());
            for (VariantImage variantImage : variantImages) {
                VariantImageDTO variantImageDTO = new VariantImageDTO();
                variantImageDTO.setId(variantImage.getId());
                variantImageDTO.setImage_url(variantImage.getImage_url());
                variantImageDTOS.add(variantImageDTO);
            }
            variantDTO.setImages(variantImageDTOS);
            list.add(variantDTO);
        }
        productDTO.setVariants(list);
        return productDTO;
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy id sản phẩm"));
        productRepository.delete(product);
    }


}

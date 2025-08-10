package com.example.clothes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column()
    private Double price;
    @Column()
    private String productName;
    @Column()
    private String image_url;
    @Column()
    private String description;
    @Column()
    private String slug;
    private LocalDateTime create_at;
    private LocalDateTime updated_at;
    private String sex;
    @ManyToOne()
    @JoinColumn(name = "category_id")
    private Categories categories;
    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Variant> variants;
    @ManyToOne()
    @JoinColumn(name = "brand_id")
    private Brand brand;
}
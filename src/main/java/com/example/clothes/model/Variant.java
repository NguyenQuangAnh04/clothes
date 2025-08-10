package com.example.clothes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "variant")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Variant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column()
    private String size;
    @Column()
    private String color;
    @Column()
    private Integer quantity;
    @Column()
    private LocalDateTime created_at;
    @Column()
    private LocalDateTime last_updated;
    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VariantImage> variantImages;
}

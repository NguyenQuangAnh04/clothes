package com.example.clothes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String size;
    @Column()
    private BigDecimal price;
    @Column()
    private String color;
    @Column()
    private String image_url;
    @Column()
    private Integer quantity;

    @Column()
    private LocalDateTime last_updated;
    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;
}

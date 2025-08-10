package com.example.clothes.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "variant_image")
@Data
public class VariantImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String image_url;

    @Column()
    private LocalDateTime created_at;
    @Column()
    private LocalDateTime updated_at;

    @ManyToOne()
    @JoinColumn(name = "variant_id")
    private Variant variant;
}

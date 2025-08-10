package com.example.clothes.model;

import jakarta.persistence.*;


public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
}

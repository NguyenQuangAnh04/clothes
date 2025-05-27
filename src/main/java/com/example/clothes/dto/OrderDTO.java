package com.example.clothes.dto;

import com.example.clothes.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderId;
    private String customerName;
    private String address;
    private String email;
    private String phone;
    private String note;
    private PaymentMethod paymentMethod;
    private Double subtotal;
    private String status;
    private LocalDateTime create_at;
    private List<OrderItemDTO> orderItemDTOS;
}

package com.example.clothes.service;

import com.example.clothes.dto.OrderDTO;
import com.example.clothes.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface IOrderService {
    Page<OrderDTO> findAll(PageRequest pageRequest);
    OrderDTO createOrder(OrderDTO orderDTO, Long userId);
    List<OrderDTO> findOrders(Long user);
    OrderDTO findOrderDetail(Long userId, Long orderId);
    void cancelled(Long userId, Long orderId);

    ByteArrayInputStream exportInvoicePdf(Long id);
}

package com.example.clothes.service;

import com.example.clothes.dto.OrderDTO;
import com.example.clothes.enums.OrderStatus;
import com.example.clothes.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

public interface IOrderService {
    Page<OrderDTO> findAll(OrderStatus status, String fullName, Long orderId,String phone, PageRequest pageRequest);
    Order createOrder(OrderDTO orderDTO);
    List<OrderDTO> findOrders();
    OrderDTO findOrderDetail(Long userId, Long orderId);
    void cancelled(Long orderId);
    Map<String, Long> countOrdersByStatus();
    void deleteOrder(Long orderId);
    void changeStatus(Long orderId, OrderStatus orderStatus);
    ByteArrayInputStream exportInvoicePdf(Long id);
}

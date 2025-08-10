package com.example.clothes.mapper;

import com.example.clothes.dto.OrderDTO;
import com.example.clothes.dto.OrderItemDTO;
import com.example.clothes.model.Order;
import com.example.clothes.model.OrderItem;
import com.example.clothes.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapperDTO {
    @Autowired
    private OrderItemRepository orderItemRepository;

    public OrderDTO mapOrderToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setFullName(order.getCustomerName());
        orderDTO.setEmail(order.getEmail());
        orderDTO.setAddress(order.getAddress());
        orderDTO.setStatus(order.getOrderStatus().name());
        orderDTO.setSubtotal(order.getTotalAmount());
        orderDTO.setPhone(order.getPhone());
        List<OrderItem> orderItems = orderItemRepository.findByOrderItemId(order.getOrderId());
        List<OrderItemDTO> orders = orderItems.stream().map(item -> {
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setId(item.getOrderItemId());
            orderItemDTO.setProductName(item.getProduct().getProductName());
            orderItemDTO.setQuantity(item.getQuantity());
            orderItemDTO.setProductId(item.getProduct().getId());
            orderItemDTO.setSize(item.getSize());

            orderItemDTO.setPrice(item.getProduct().getPrice());
            orderItemDTO.setImage_url(item.getProduct().getImage_url());
            orderItemDTO.setColor(item.getColor());
            orderItemDTO.setTotalAmount(item.getSubtotal());
            return orderItemDTO;
        }).toList();
        orderDTO.setOrderItemDTOS(orders);
        return orderDTO;
    }


}

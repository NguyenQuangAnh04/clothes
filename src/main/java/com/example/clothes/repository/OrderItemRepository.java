package com.example.clothes.repository;

import com.example.clothes.model.Order;
import com.example.clothes.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Objects;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);

    @Query("SELECT o FROM OrderItem o WHERE o.order.orderId = :orderId")
    List<OrderItem> findByOrderItemId(@Param("orderId") Long orderId);


}

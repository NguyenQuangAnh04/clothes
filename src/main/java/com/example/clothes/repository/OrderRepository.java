package com.example.clothes.repository;

import com.example.clothes.model.Order;
import com.example.clothes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o from Order o where o.user = :user")
    List<Order> findAllUser(@Param("user") User user);
    Optional<Order> findByUser(User user);

    Optional<Order> findByOrderIdAndUser(Long orderId, User userId);

}

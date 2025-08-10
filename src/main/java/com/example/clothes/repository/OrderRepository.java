package com.example.clothes.repository;

import com.example.clothes.enums.OrderStatus;
import com.example.clothes.model.Order;
import com.example.clothes.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o from Order o where o.user = :user")
    List<Order> findAllUser(@Param("user") User user);

    @Query("SELECT o FROM Order o WHERE (:status is null or o.orderStatus = :status) " +
            "AND (:id is null or o.orderId = :id) AND " +
            "(:fullName is null or o.customerName LIKE %:fullName%) AND (:phone is null OR o.phone = :phone)")
    Page<Order> findAllByStatusAndIdAndFullName(@Param("status") OrderStatus status, @Param("id") Long id
            , @Param("fullName") String fullName, @Param("phone") String phone
            , PageRequest pageRequest);

    Optional<Order> findByOrderIdAndUser(Long orderId, User userId);

    @Query("""
                SELECT COALESCE(SUM(o.totalAmount), 0) 
                FROM Order o
                WHERE YEAR(o.create_at) = :year
                  AND MONTH(o.create_at) = :month
                  AND o.orderStatus = 'SHIPPED'
            """)
    Double getRevenueByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(o) FROM Order o where FUNCTION('YEAR', o.create_at) = :year" +
            " AND FUNCTION('MONTH', o.create_at) = :month ")
    Long totalOrderInMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(o.customerName) FROM Order o WHERE YEAR(o.create_at) = :year AND MONTH(o.create_at) = :month")
    Long countNewCustomersInMonth(@Param("year") int year, @Param("month") int month);

}

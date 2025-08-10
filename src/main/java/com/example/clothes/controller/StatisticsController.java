package com.example.clothes.controller;

import com.example.clothes.repository.OrderRepository;
import com.example.clothes.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private OrderRepository orderRepository;
    @GetMapping("/dashboard/summary")
    public ResponseEntity<Map<String, Object>> DashboardSummary (@RequestParam(name = "year", required = false) Integer year
            , @RequestParam(name = "month", required = false)  Integer month) {
        LocalDate now = LocalDate.now();

        int queryYear = (year != null) ? year : now.getYear();
        int queryMonth = (month != null) ? month : now.getMonthValue();

        Long totalOrders = orderRepository.totalOrderInMonth(queryYear, queryMonth);
        Double totalRevenue = orderRepository.getRevenueByYearAndMonth(queryYear, queryMonth);
        Long newCustomers = statisticsService.countNewCustomersCurrentMonth();

        Map<String, Object> response = new HashMap<>();
        response.put("totalOrders", totalOrders);
        response.put("totalRevenue", totalRevenue);
        response.put("newCustomers", newCustomers);
        return ResponseEntity.ok(response);
    }


}

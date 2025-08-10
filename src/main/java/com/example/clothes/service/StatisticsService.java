package com.example.clothes.service;

import com.example.clothes.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService implements IStatisticsService {
    @Autowired
    private OrderRepository orderRepository;


    @Override
    public Long countNewCustomersCurrentMonth() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return orderRepository.countNewCustomersInMonth(year, month);
    }

    @Override
    public Long totalOrderInMonth(int year, int month) {
      return orderRepository.totalOrderInMonth(year, month);
    }
}

package com.example.clothes.service;

import com.example.clothes.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService implements IStatisticsService {
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Map<Integer, Double> getMonthlyRevenue(Long year) {
        List<Object[]> results = orderRepository.getMonthlyRevenue(year);
        Map<Integer, Double> revenueByMonth = new HashMap<>();
        for (Object[] row : results) {
            Integer month = (Integer) row[0];
            Double total = (Double) row[1];
            if (total != 0.0 && total != null) {
                revenueByMonth.put(month, total);
            }
        }
        return revenueByMonth;
    }
}

package com.example.clothes.service;

import java.util.Map;

public interface IStatisticsService {
    Map<Integer, Double> getMonthlyRevenue(Long year);
}

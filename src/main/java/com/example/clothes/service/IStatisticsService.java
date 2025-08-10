package com.example.clothes.service;

import java.util.Map;

public interface IStatisticsService {
    Long countNewCustomersCurrentMonth();

    Long totalOrderInMonth(int year, int month);
}

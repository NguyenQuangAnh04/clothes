package com.example.clothes.controller;

import com.example.clothes.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/revenue/monthly")
    public ResponseEntity<Map<Integer, Double>> getMonthlyRevenue(@RequestParam(name = "year") Long year) {
        Map<Integer, Double> data = statisticsService.getMonthlyRevenue(year);
        return ResponseEntity.ok(data);
    }
}

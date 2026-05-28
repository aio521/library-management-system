package com.library.controller;

import com.library.common.Result;
import com.library.service.StatisticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@PreAuthorize("hasAnyAuthority('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/borrow/overview")
    public Result<Map<String, Object>> borrowOverview() {
        return Result.success(statisticsService.borrowOverview());
    }

    @GetMapping("/books/popular")
    public Result<List<Map<String, Object>>> popularBooks(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(statisticsService.popularBooks(limit));
    }

    @GetMapping("/books/category")
    public Result<List<Map<String, Object>>> categoryStats() {
        return Result.success(statisticsService.categoryStats());
    }

    @GetMapping("/readers/active")
    public Result<List<Map<String, Object>>> activeReaders(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(statisticsService.activeReaders(limit));
    }

    @GetMapping("/borrow/trend")
    public Result<List<Map<String, Object>>> borrowTrend(@RequestParam(defaultValue = "day") String period) {
        return Result.success(statisticsService.borrowTrend(period));
    }
}

package com.library.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> borrowOverview();
    List<Map<String, Object>> popularBooks(Integer limit);
    List<Map<String, Object>> categoryStats();
    List<Map<String, Object>> activeReaders(Integer limit);
    List<Map<String, Object>> borrowTrend(String period);
}

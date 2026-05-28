package com.library.service.impl;

import com.library.service.StatisticsService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final JdbcTemplate jdbcTemplate;

    public StatisticsServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> borrowOverview() {
        Map<String, Object> result = new LinkedHashMap<>();

        Integer today = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM borrow_record WHERE DATE(borrow_date) = CURDATE()", Integer.class);
        Integer month = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM borrow_record WHERE DATE_FORMAT(borrow_date, '%Y%m') = DATE_FORMAT(CURDATE(), '%Y%m')", Integer.class);
        Integer year = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM borrow_record WHERE YEAR(borrow_date) = YEAR(CURDATE())", Integer.class);
        Integer overdue = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM borrow_record WHERE due_date < CURDATE() AND status IN (0, 3)", Integer.class);

        result.put("todayBorrow", today != null ? today : 0);
        result.put("monthBorrow", month != null ? month : 0);
        result.put("yearBorrow", year != null ? year : 0);
        result.put("overdueCount", overdue != null ? overdue : 0);

        return result;
    }

    @Override
    public List<Map<String, Object>> popularBooks(Integer limit) {
        return jdbcTemplate.queryForList(
                "SELECT b.id, b.title, b.author, COUNT(br.id) AS borrow_count " +
                "FROM borrow_record br JOIN book_stock bs ON br.stock_id = bs.id " +
                "JOIN book b ON bs.book_id = b.id " +
                "GROUP BY b.id ORDER BY borrow_count DESC LIMIT ?", limit);
    }

    @Override
    public List<Map<String, Object>> categoryStats() {
        return jdbcTemplate.queryForList(
                "SELECT c.name AS category_name, COUNT(br.id) AS borrow_count " +
                "FROM borrow_record br JOIN book_stock bs ON br.stock_id = bs.id " +
                "JOIN book b ON bs.book_id = b.id " +
                "LEFT JOIN category c ON b.category_id = c.id " +
                "GROUP BY c.id, c.name ORDER BY borrow_count DESC");
    }

    @Override
    public List<Map<String, Object>> activeReaders(Integer limit) {
        return jdbcTemplate.queryForList(
                "SELECT r.id, r.name, r.reader_no, r.dept, COUNT(br.id) AS borrow_count " +
                "FROM borrow_record br JOIN reader r ON br.reader_id = r.id " +
                "GROUP BY r.id ORDER BY borrow_count DESC LIMIT ?", limit);
    }

    @Override
    public List<Map<String, Object>> borrowTrend(String period) {
        // 白名单校验，杜绝注入风险（DATE_FORMAT 格式参数无法用占位符）
        String format = switch (period) {
            case "week" -> "%Y-%u";
            case "month" -> "%Y-%m";
            default -> "%Y-%m-%d";
        };

        return jdbcTemplate.queryForList(
                "SELECT DATE_FORMAT(borrow_date, ?) AS period, COUNT(*) AS count " +
                "FROM borrow_record GROUP BY period ORDER BY period", format);
    }
}

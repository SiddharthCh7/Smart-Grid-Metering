package com.example.smartgridmetering.Utils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FeatureUtils {

    private final JdbcTemplate jdbcTemplate;

    public FeatureUtils(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Returns latest day's features ready for ML model */
    public List<Double> getLatestFeatures() {
        List<Map<String, Object>> data = fetchLast14DaysAggregated();
        List<Map<String, Object>> engineered = generateFeatures(data);
        return getLastRowFeatures(engineered);
    }

    /** Fetch last 14 days aggregated energy per day */
    private List<Map<String, Object>> fetchLast14DaysAggregated() {
        String sql = "SELECT DATE(datetime) AS date, SUM(energy_consumed) AS daily_energy " +
                "FROM energy_consumption_info " +
                "GROUP BY DATE(datetime) " +
                "ORDER BY DATE(datetime) DESC " +
                "LIMIT 14";

        List<Map<String, Object>> rowsDesc = jdbcTemplate.queryForList(sql);

        // Reverse to ascending order for correct lag/rolling calculation
        Collections.reverse(rowsDesc);

        // Convert SQL DATE to LocalDate and rename column
        return rowsDesc.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("datetime", ((java.sql.Date) row.get("date")).toLocalDate().atStartOfDay());
                    map.put("energy_consumed", ((Number) row.get("daily_energy")).doubleValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /** Generate time-based + lag + rolling features */
    private List<Map<String, Object>> generateFeatures(List<Map<String, Object>> data) {
        List<Double> energy = data.stream()
                .map(d -> (Double) d.get("energy_consumed"))
                .collect(Collectors.toList());

        for (int i = 0; i < data.size(); i++) {
            LocalDate dt = ((java.time.LocalDateTime) data.get(i).get("datetime")).toLocalDate();

            // Time features
            data.get(i).put("day_of_week", (double) dt.getDayOfWeek().getValue());
            data.get(i).put("month", (double) dt.getMonthValue());
            data.get(i).put("is_weekend", (dt.getDayOfWeek() == DayOfWeek.SATURDAY || dt.getDayOfWeek() == DayOfWeek.SUNDAY) ? 1.0 : 0.0);

            // Lag features
            data.get(i).put("lag_1_day", i >= 1 ? energy.get(i - 1) : 0.0);
            data.get(i).put("lag_7_day", i >= 7 ? energy.get(i - 7) : 0.0);
            data.get(i).put("lag_14_day", i >= 14 ? energy.get(i - 14) : 0.0);

            // Rolling mean / std
            data.get(i).put("roll_mean_1_day", energy.get(i));
            data.get(i).put("roll_mean_7_day", i >= 6 ? mean(energy.subList(i - 6, i + 1)) : 0.0);
            data.get(i).put("roll_mean_14_day", i >= 13 ? mean(energy.subList(i - 13, i + 1)) : 0.0);

            data.get(i).put("roll_std_7_day", i >= 6 ? stdDev(energy.subList(i - 6, i + 1)) : 0.0);
            data.get(i).put("roll_std_14_day", i >= 13 ? stdDev(energy.subList(i - 13, i + 1)) : 0.0);
        }

        return data;
    }

    /** Extract latest row features */
    private List<Double> getLastRowFeatures(List<Map<String, Object>> data) {
        Map<String, Object> lastRow = data.get(data.size() - 1);

        return Arrays.asList(
                (Double) lastRow.get("day_of_week"),
                (Double) lastRow.get("month"),
                (Double) lastRow.get("is_weekend"),
                (Double) lastRow.get("lag_1_day"),
                (Double) lastRow.get("lag_7_day"),
                (Double) lastRow.get("lag_14_day"),
                (Double) lastRow.get("roll_mean_1_day"),
                (Double) lastRow.get("roll_mean_7_day"),
                (Double) lastRow.get("roll_mean_14_day"),
                (Double) lastRow.get("roll_std_7_day"),
                (Double) lastRow.get("roll_std_14_day")
        );
    }

    private Double mean(List<Double> values) {
        return values.stream().mapToDouble(d -> d).average().orElse(0.0);
    }

    private Double stdDev(List<Double> values) {
        double m = mean(values);
        double variance = values.stream().mapToDouble(d -> Math.pow(d - m, 2)).sum() / values.size();
        return Math.sqrt(variance);
    }
}

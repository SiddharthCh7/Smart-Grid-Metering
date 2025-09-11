package com.example.smartgridmetering.Repository;

import com.example.smartgridmetering.Model.User;
import com.example.smartgridmetering.Model.EnergyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface EnergyConsumptionRepository extends JpaRepository<EnergyConsumption, Long> {

    // getting last 30day energy consumption (for energy consumption history)
    @Query(value = """
    SELECT SUM(energy_consumed) AS total
    FROM energy_consumption_info
    WHERE user_id = :userId
      AND datetime >= NOW() - interval '30 day'
      AND datetime <= NOW()
    GROUP BY DATE_TRUNC('day', datetime) + INTERVAL '3 hour' * FLOOR(EXTRACT(HOUR FROM datetime)/3)
    ORDER BY DATE_TRUNC('day', datetime) + INTERVAL '3 hour' * FLOOR(EXTRACT(HOUR FROM datetime)/3) DESC
""", nativeQuery = true)
    List<BigDecimal> Last30dayEnergyConsumption(@Param("userId") Long userId);


    // getting latest (last hour) energy consumption
    @Query(value = """
    SELECT COALESCE(SUM(energy_consumed), 0) AS last_hour
    FROM energy_consumption_info
    WHERE user_id = :userId
      AND datetime >= NOW() - interval '1 hour'
      AND datetime <= NOW()
    """, nativeQuery = true)
    BigDecimal lastHourEnergyConsumption(@Param("userId") Long userId);

    // getting last month bill (last month energy consumed * 10.00(price for each unit))
    @Query(value= """
        SELECT
        COALESCE(SUM(energy_consumed) * 10.00, 0) AS last_month_bill
        FROM energy_consumption_info
        WHERE user_id = :userId
            AND datetime >= DATE_TRUNC('month', NOW() - INTERVAL '1 month')
            AND datetime < DATE_TRUNC('month', NOW());
    """, nativeQuery = true)
    BigDecimal lastMonthbill(@Param("userId") Long userId);


    @Query(value= """

       SELECT SUM(energy_consumed) AS total_today
        FROM energy_consumption_info
        WHERE user_id = :userId
          AND datetime >= CURRENT_DATE
          AND datetime < CURRENT_DATE + INTERVAL '1 day';
    """, nativeQuery = true)
    BigDecimal todayEnergyConsumption(@Param("userId") Long userId);
}




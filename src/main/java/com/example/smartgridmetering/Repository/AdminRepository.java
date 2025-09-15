package com.example.smartgridmetering.Repository;

import com.example.smartgridmetering.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // 1. Total number of customers
    @Query("SELECT COUNT(u) FROM User u")
    long getTotalCustomers();

    // 2. Total energy consumed today (across all users)
    @Query(value = """
        SELECT COALESCE(SUM(e.energy_consumed), 0)
        FROM energy_consumption_info e
        WHERE e.datetime >= CURRENT_DATE
      """, nativeQuery = true)
    BigDecimal getEnergyConsumedToday();

    // Revenue today
    @Query(value = """
        SELECT COALESCE(SUM(e.energy_consumed) * :PricePerUnit, 0)
            FROM energy_consumption_info e
            WHERE e.datetime >= DATE_TRUNC('day', CURRENT_DATE)
    """, nativeQuery = true)
    BigDecimal getRevenueToday(@Param("PricePerUnit") double PricePerUnit);

    // Percentage growth by the previous day
    @Query(value = """
    SELECT 
        COALESCE(
            (SELECT SUM(e.energy_consumed) * :PricePerUnit 
             FROM energy_consumption_info e 
             WHERE e.datetime >= DATE_TRUNC('day', CURRENT_DATE))
            /
            NULLIF(
                (SELECT SUM(e.energy_consumed) * :PricePerUnit 
                 FROM energy_consumption_info e 
                 WHERE e.datetime >= DATE_TRUNC('day', CURRENT_DATE - INTERVAL '1 day')
                   AND e.datetime < DATE_TRUNC('day', CURRENT_DATE)),
                0
            )
        , 0)
""", nativeQuery = true)
    BigDecimal getPercentageGrowthVsPreviousDay(@Param("PricePerUnit") double PricePerUnit);

    // Revenue this month till date.
    @Query(value = """
        SELECT SUM(e.energy_consumed) * :PricePerUnit
        FROM energy_consumption_info e
        WHERE e.datetime >= DATE_TRUNC('month', CURRENT_DATE)
          AND e.datetime < DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month'
        """,
            nativeQuery = true)
    BigDecimal getRevenueThisMonth(@Param("PricePerUnit") double PricePerUnit);

    // Percentage growth vs last month
    @Query(value = """
    SELECT 
        COALESCE(
            (SELECT SUM(e.energy_consumed) * :PricePerUnit 
             FROM energy_consumption_info e 
             WHERE e.datetime >= DATE_TRUNC('month', CURRENT_DATE))
            /
            NULLIF(
                (SELECT SUM(e.energy_consumed) * :PricePerUnit 
                 FROM energy_consumption_info e 
                 WHERE e.datetime >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
                   AND e.datetime < DATE_TRUNC('month', CURRENT_DATE)),
                0
            )
        , 0)
""", nativeQuery = true)
    BigDecimal getPercentageGrowthVsLastMonth(@Param("PricePerUnit") double PricePerUnit);

}


package com.example.smartgridmetering.Service;

import com.example.smartgridmetering.Model.EnergyConsumption;
import com.example.smartgridmetering.Model.User;
import com.example.smartgridmetering.Repository.EnergyConsumptionRepository;
import com.example.smartgridmetering.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EnergyConsumptionRepository energyConsumptionRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Fetch last 30 days 3-hour interval sums from repository
     * and split them into 24H, 7D, and 30D lists.
     */
    public Map<String, List<BigDecimal>> getEnergyConsumptionForChart(User user) {
        // Fetch all 3-hour interval sums for last 30 days, latest first
        List<BigDecimal> last30Days = energyConsumptionRepository.Last30dayEnergyConsumption(user.getId());

        Map<String, List<BigDecimal>> result = new HashMap<>();

        // 24H → 8 intervals (24 / 3)
        List<BigDecimal> last24H = last30Days.stream()
                .limit(8)
                .collect(Collectors.toList());

        // 7D → 7 days * 8 intervals/day = 56 intervals
        List<BigDecimal> last7D = last30Days.stream()
                .limit(56)
                .collect(Collectors.toList());

        // 30D → all intervals
        List<BigDecimal> last30D = new ArrayList<>(last30Days);

        result.put("24H", last24H);
        result.put("7D", last7D);
        result.put("30D", last30D);

        return result;
    }

    // max value across all intervals for chart scaling
    public BigDecimal getMaxEnergy(List<BigDecimal> intervals) {
        return intervals.stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ONE);
    }

    public BigDecimal lastHourEnergyConsumption(User user) {
        return energyConsumptionRepository.lastHourEnergyConsumption(user.getId());
    }

    public BigDecimal lastMonthbill(User user) {
        return energyConsumptionRepository.lastMonthbill(user.getId());
    }

    public BigDecimal todayEnergyConsumption(User user) {
        return energyConsumptionRepository.todayEnergyConsumption(user.getId());
    }
}
package com.example.smartgridmetering.Service;

import com.example.smartgridmetering.Model.User;
import com.example.smartgridmetering.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminService {

    @Autowired
    private  AdminRepository adminRepository;

    public User getAdminByEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    double PricePerUnit = 10.00;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public long getTotalCustomers() {
        return adminRepository.getTotalCustomers();
    }

    public BigDecimal getEnergyConsumedToday() {
        return adminRepository.getEnergyConsumedToday();
    }

    public BigDecimal getRevenueToday() {
        return adminRepository.getRevenueToday(PricePerUnit);
    }

    public BigDecimal getPercentageGrowthVsPreviousDay() {
        return adminRepository.getPercentageGrowthVsPreviousDay(PricePerUnit);
    }

    public BigDecimal getRevenueThisMonth() {
        return adminRepository.getRevenueThisMonth(PricePerUnit);
    }

    public BigDecimal getPercentageGrowthVsLastMonth(){
        return adminRepository.getPercentageGrowthVsLastMonth(PricePerUnit);
    }

}


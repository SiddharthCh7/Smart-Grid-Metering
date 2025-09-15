package com.example.smartgridmetering.Controller;


import com.example.smartgridmetering.Model.User;
import com.example.smartgridmetering.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.smartgridmetering.Client.MLClient;
import com.example.smartgridmetering.Utils.FeatureUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private final AdminService adminService;
    private final FeatureUtils featureUtils;
    private final MLClient mlClient;

    public AdminController(AdminService adminService, FeatureUtils featureUtils, MLClient mlClient) {
        this.adminService = adminService;
        this.featureUtils = featureUtils;
        this.mlClient = mlClient;
    }

    @GetMapping
    public String dashboard(Model model, Principal principal) {
        User admin = adminService.getAdminByEmail(principal.getName());

        model.addAttribute("admin", admin);

        model.addAttribute("totalCustomers", adminService.getTotalCustomers());
        model.addAttribute("energyConsumedToday", adminService.getEnergyConsumedToday());

        // Today
        model.addAttribute("revenueToday", adminService.getRevenueToday());
        model.addAttribute("getPercentageGrowthVsPreviousDay",  adminService.getPercentageGrowthVsPreviousDay());

        // This Month
        model.addAttribute("revenueThisMonth", adminService.getRevenueThisMonth());
        model.addAttribute("getPercentageGrowthVsLastMonth", adminService.getPercentageGrowthVsLastMonth());

        // Initial prediction
        List<Double> features = featureUtils.getLatestFeatures();
        Double prediction = mlClient.getPrediction(features);
        Double confidence = 94.2;
        model.addAttribute("prediction", prediction);
        model.addAttribute("confidence", confidence);

        return "admin/adminIndex"; // Thymeleaf template
    }

    @GetMapping("/next-day-prediction")
    public Map<String, Object> getNextDayPrediction() {
        List<Double> features = featureUtils.getLatestFeatures();
        Double prediction = mlClient.getPrediction(features);
        Double confidence = 94.2; // Or compute dynamically if your model provides it

        return Map.of(
                "prediction", prediction,
                "confidence", confidence
        );
    }
}


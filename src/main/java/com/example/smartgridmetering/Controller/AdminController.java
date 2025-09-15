package com.example.smartgridmetering.Controller;


import com.example.smartgridmetering.Model.User;
import com.example.smartgridmetering.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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
        return "admin/adminIndex"; // Thymeleaf template
    }
}


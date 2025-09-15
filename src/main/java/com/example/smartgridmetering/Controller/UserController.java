package com.example.smartgridmetering.Controller;

import com.example.smartgridmetering.Model.User;
//import com.example.smartgridmetering.Repository.EnergyConsumptionRepository;
import org.springframework.ui.Model;
import com.example.smartgridmetering.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Controller
public class UserController {

    @Autowired private UserService userService;
//    @Autowired private EnergyConsumptionRepository energyRepo;

    @GetMapping("/user")
    public String dashboard(Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());

        Map<String, List<BigDecimal>> energyData = userService.getEnergyConsumptionForChart(user);
        model.addAttribute("energy24H", energyData.get("24H"));
        model.addAttribute("energy7D", energyData.get("7D"));
        model.addAttribute("energy30D", energyData.get("30D"));

        // max for chart scaling
        BigDecimal maxEnergy = Stream.of(
                energyData.get("24H").stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO),
                energyData.get("7D").stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO),
                energyData.get("30D").stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO)
        ).max(BigDecimal::compareTo).orElse(BigDecimal.ONE);

        BigDecimal lastHourEnergyConsumption = userService.lastHourEnergyConsumption(user);
        model.addAttribute("lastHourEnergyConsumption", lastHourEnergyConsumption);

        BigDecimal lastMonthbill =  userService.lastMonthbill(user);
        model.addAttribute("lastMonthbill", lastMonthbill);

        BigDecimal todayEnergyConsumption =  userService.todayEnergyConsumption(user);
        model.addAttribute("todayEnergyConsumption", todayEnergyConsumption);

        model.addAttribute("maxEnergy", maxEnergy);
        model.addAttribute("user", user);

        return "user/userIndex";
    }


}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.safeguardsa.controllers;

import com.safeguardsa.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
/**
 *
 * @author ntsak
 */
@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", dashboardService.getStatCards());
        model.addAttribute("categoryChart", dashboardService.getTipsByCategoryChart());
        model.addAttribute("provinceChart", dashboardService.getTipsByProvinceChart());
        model.addAttribute("monthlyChart", dashboardService.getMonthlyTrendChart());
        return "dashboard";
    }
}

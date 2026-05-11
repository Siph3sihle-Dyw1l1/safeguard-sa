package com.safeguardsa.controllers;

import com.safeguardsa.models.SafetyTip;
import com.safeguardsa.repositories.SafetyTipRepository;
import com.safeguardsa.ejbs.StatsEJB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class AdminTipController {

    @Autowired
    private SafetyTipRepository safetyTipRepository;

    @Autowired
    private StatsEJB statsEJB;

    // FIX: Removed @GetMapping("/admin/dashboard") to prevent conflict with DashboardController 
    @GetMapping("/admin/tips")
    public String viewTips(@RequestParam(required = false) String status, Model model) {
        List<SafetyTip> tips = (status != null && !status.isEmpty())
                ? safetyTipRepository.findByStatus(status)
                : safetyTipRepository.findAll();

        model.addAttribute("tips", tips);
        model.addAttribute("totalTips", safetyTipRepository.count());
        model.addAttribute("pendingCount", safetyTipRepository.countByStatus("PENDING"));
        model.addAttribute("approvedCount", safetyTipRepository.countByStatus("APPROVED"));

        return "admin-tips"; // Thymeleaf handles the .html suffix 
    }

    @PostMapping("/admin/tips/{id}/approve")
    public String approveTip(@PathVariable Long id) {
        SafetyTip tip = safetyTipRepository.findById(id).orElse(null);
        if (tip != null) {
            tip.setStatus("APPROVED");
            safetyTipRepository.save(tip);
            statsEJB.invalidateCache();
        }
        return "redirect:/admin/tips";
    }

    // ── Flag a tip ───────────────────────────────────────────────────────────
    @PostMapping("/admin/tips/{id}/flag")
    public String flagTip(@PathVariable Long id) {
        SafetyTip tip = safetyTipRepository.findById(id).orElse(null);
        if (tip != null) {
            tip.setStatus("FLAGGED"); // Sets status to FLAGGED so it hides from the map
            safetyTipRepository.save(tip);
            statsEJB.invalidateCache();
        }
        return "redirect:/admin/tips"; // Reloads the page
    }

    // ── Delete a tip ─────────────────────────────────────────────────────────
    @PostMapping("/admin/tips/{id}/delete")
    public String deleteTip(@PathVariable Long id) {
        if (safetyTipRepository.existsById(id)) {
            safetyTipRepository.deleteById(id); // Wipes it from the database entirely
            statsEJB.invalidateCache();
        }
        return "redirect:/admin/tips"; // Reloads the page
    }
}

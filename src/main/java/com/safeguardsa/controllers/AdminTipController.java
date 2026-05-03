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

    // ── Show all tips (with optional filter) ────────────────────────────────
    @GetMapping("/admin/tips")
    public String viewTips(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String category,
            Model model) {

        List<SafetyTip> tips;

        // Filter logic
        if (status != null && !status.isEmpty()) {
            tips = safetyTipRepository.findByStatus(status);
        } else if (province != null && !province.isEmpty()) {
            tips = safetyTipRepository.findByProvince(province);
        } else if (category != null && !category.isEmpty()) {
            tips = safetyTipRepository.findByCategory(category);
        } else {
            tips = safetyTipRepository.findAll();
        }

        // Pass data to the HTML page
        model.addAttribute("tips", tips);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedProvince", province);
        model.addAttribute("selectedCategory", category);

        // Stat counts for the top of the page
        model.addAttribute("totalTips", safetyTipRepository.count());
        model.addAttribute("pendingCount", safetyTipRepository.countByStatus("PENDING"));
        model.addAttribute("approvedCount", safetyTipRepository.countByStatus("APPROVED"));
        model.addAttribute("flaggedCount", safetyTipRepository.countByStatus("FLAGGED"));

        return "admin-tips";
    }

    // ── Approve a tip ────────────────────────────────────────────────────────
    @PostMapping("/admin/tips/{id}/approve")
    public String approveTip(@PathVariable Long id) {
        SafetyTip tip = safetyTipRepository.findById(id).orElse(null);
        if (tip != null) {
            tip.setStatus("APPROVED");
            safetyTipRepository.save(tip);
            statsEJB.invalidateCache(); // Refresh dashboard stats
        }
        return "redirect:/admin/tips";
    }

    // ── Flag a tip ───────────────────────────────────────────────────────────
    @PostMapping("/admin/tips/{id}/flag")
    public String flagTip(@PathVariable Long id) {
        SafetyTip tip = safetyTipRepository.findById(id).orElse(null);
        if (tip != null) {
            tip.setStatus("FLAGGED");
            safetyTipRepository.save(tip);
            statsEJB.invalidateCache();
        }
        return "redirect:/admin/tips";
    }

    // ── Delete a tip ─────────────────────────────────────────────────────────
    @PostMapping("/admin/tips/{id}/delete")
    public String deleteTip(@PathVariable Long id) {
        safetyTipRepository.deleteById(id);
        statsEJB.invalidateCache();
        return "redirect:/admin/tips";
    }
}

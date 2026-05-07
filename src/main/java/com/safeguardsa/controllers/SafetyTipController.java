package com.safeguardsa.controllers;

import com.safeguardsa.services.SafetyTipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * SafetyTipController — Member C Handles anonymous safety tip submission form.
 * GET /tip — renders tip-form.html POST /tip/submit — validates and saves the
 * tip, then redirects to /map
 *
 * No name, email, or phone number is ever collected — by design.
 */
@Controller
@RequestMapping("/tip")
public class SafetyTipController {

    @Autowired
    private SafetyTipService safetyTipService;

    /**
     * GET /tip Displays the anonymous safety tip submission form.Populates
     * province, category, and time-of-day dropdowns.
     *
     * @param model
     * @return
     */
    @GetMapping
    public String showTipForm(Model model) {
        model.addAttribute("provinces", safetyTipService.getAllProvinces());
        model.addAttribute("categories", safetyTipService.getAllCategories());
        model.addAttribute("timesOfDay", safetyTipService.getTimesOfDay());
        return "tip-form";
    }

    /**
     * POST /tip/submit Accepts the anonymous tip form fields.Fields collected:
     * province, city, streetArea, category, description, timeOfDay.No personal
     * information (name, email, phone) is accepted or stored.On success →
     * redirect to /map with a success flash message.On error → redirect back to
     * /tip with an error flash message.
     *
     * @param province
     * @param city
     * @param streetArea
     * @param category
     * @param description
     * @param timeOfDay
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/submit")
    public String submitTip(
            @RequestParam String province,
            @RequestParam String city,
            @RequestParam String streetArea,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String timeOfDay,
            RedirectAttributes redirectAttributes) {

        try {
            safetyTipService.submitTip(province, city, streetArea, category, description, timeOfDay);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thank you! Your safety tip has been submitted and will appear on the map after review.");
            return "redirect:/map";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tip";
        }
    }
}

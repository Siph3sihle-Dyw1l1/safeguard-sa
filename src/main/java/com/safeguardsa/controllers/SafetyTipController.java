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

@Controller
@RequestMapping("/tip")
public class SafetyTipController {

    @Autowired
    private SafetyTipService safetyTipService;

    /*
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

    /*
     * @param province
     * @param city
     * @param streetArea
     * @param category
     * @param description
     * @param timeOfDay
     * @param latitude
     * @param longitude
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
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            RedirectAttributes redirectAttributes) {

        try {

            safetyTipService.submitTip(province, city, streetArea, category, description, timeOfDay, latitude, longitude);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Thank you! Your safety tip has been submitted and will appear on the map after review.");
            return "redirect:/map";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tip";
        }
    }
}

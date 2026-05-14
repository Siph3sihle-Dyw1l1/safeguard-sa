package com.safeguardsa.controllers;

import com.safeguardsa.models.SafetyTip;
import com.safeguardsa.services.SafetyMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/map")
public class SafetyMapController {

    @Autowired
    private SafetyMapService safetyMapService;

    /*
     * @param model
     * @return
     */
    @GetMapping
    public String showMap(Model model) {
        model.addAttribute("provinces", safetyMapService.getAllProvinces());
        model.addAttribute("categories", safetyMapService.getAllCategories());
        return "map";
    }

    /*
     * @return
     */
 /*
     * @return
     */
    @GetMapping("/tips")
    @ResponseBody
    public List<Map<String, Object>> getApprovedTips() {

        return safetyMapService.getApprovedTipsForMap();
    }
}

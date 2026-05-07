package com.safeguardsa.controllers;

import com.safeguardsa.services.SafetyMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * SafetyMapController — Member C
 * Serves the public student safety map at /map.
 * GET /map      — renders map.html with province + category filter options
 * GET /map/tips — REST endpoint called by map.js via fetch(), returns approved tips as JSON
 */
@Controller
@RequestMapping("/map")
public class SafetyMapController {

    @Autowired
    private SafetyMapService safetyMapService;

    /**
     * GET /map
     * Renders the public safety map page (map.html).Passes province and category lists to populate the filter dropdowns.
     * @param model
     * @return 
     */
    @GetMapping
    public String showMap(Model model) {
        model.addAttribute("provinces", safetyMapService.getAllProvinces());
        model.addAttribute("categories", safetyMapService.getAllCategories());
        return "map";
    }

    /**
     * GET /map/tips
     * Called by Leaflet.js in map.js via fetch('/map/tips').Returns all APPROVED safety tips as JSON with coordinates and metadata.
     * @return
     */
    @GetMapping("/tips")
    @ResponseBody
    public List<Map<String, Object>> getApprovedTips() {
        return safetyMapService.getApprovedTipsForMap();
    }
}

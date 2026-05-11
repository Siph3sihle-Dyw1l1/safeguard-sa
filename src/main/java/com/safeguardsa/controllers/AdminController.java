package com.safeguardsa.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AdminController - Admin endpoints for MedQA data management.
 *
 * @author mmaphutijkgomo
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    /**
     * Trigger ingestion (placeholder for now).
     *
     * @return
     */
    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> triggerIngestion() {

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Ingestion endpoint ready (AI not enabled yet).");

        return ResponseEntity.ok(response);
    }

    /**
     * Check ingestion status (placeholder).
     *
     * @return
     */
    @GetMapping("/ingest/status")
    public ResponseEntity<Map<String, Object>> getStatus() {

        Map<String, Object> status = new HashMap<>();
        status.put("status", "NOT_STARTED");

        return ResponseEntity.ok(status);
    }

    /**
     * Add entry manually (placeholder).
     *
     * @param request
     * @return
     */
    @PostMapping("/medical/add")
    public ResponseEntity<Map<String, Object>> addMedicalEntry(
            @RequestBody Map<String, String> request) {

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", "Manual entry endpoint ready (AI not enabled yet).");

        return ResponseEntity.ok(response);
    }
}

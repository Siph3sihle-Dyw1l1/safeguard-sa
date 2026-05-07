package com.safeguardsa.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MedicalDataService - Handles MedQA ingestion (placeholder version).
 *
 * @author mmaphutijkgomo
 */
@Service
public class MedicalDataService {

    private final AtomicBoolean isIngesting = new AtomicBoolean(false);
    private final AtomicInteger processed = new AtomicInteger(0);

    /**
     * Simulates MedQA ingestion process.
     *
     * @return
     */
    public IngestionResult ingestMedQAData() {

        if (isIngesting.get()) {
            return new IngestionResult(false, "Already running", processed.get());
        }

        isIngesting.set(true);

        try {
            // Simulated processing (replace later with real vector store ingestion)
            Thread.sleep(2000);

            processed.set(100);

            return new IngestionResult(
                    true,
                    "MedQA ingestion completed (simulated)",
                    processed.get()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new IngestionResult(false, "Ingestion interrupted", 0);

        } finally {
            isIngesting.set(false);
        }
    }

    /**
     * Returns current ingestion status.
     *
     * @return
     */
    public IngestionStatus getStatus() {
        return new IngestionStatus(
                isIngesting.get() ? "IN_PROGRESS" : "IDLE",
                processed.get()
        );
    }

    /**
     * Placeholder for manually adding medical entries.
     *
     * @param question
     * @param answer
     * @param category
     * @return
     */
    public boolean addEntry(String question, String answer, String category) {
        return question != null && answer != null;
    }

    // ─────────────────────────────────────────────
    // DTO CLASSES
    // ─────────────────────────────────────────────
    public static class IngestionResult {

        private final boolean success;
        private final String message;
        private final int processed;

        public IngestionResult(boolean success, String message, int processed) {
            this.success = success;
            this.message = message;
            this.processed = processed;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public int getProcessed() {
            return processed;
        }
    }

    public static class IngestionStatus {

        private final String status;
        private final int processed;

        public IngestionStatus(String status, int processed) {
            this.status = status;
            this.processed = processed;
        }

        public String getStatus() {
            return status;
        }

        public int getProcessed() {
            return processed;
        }
    }
}

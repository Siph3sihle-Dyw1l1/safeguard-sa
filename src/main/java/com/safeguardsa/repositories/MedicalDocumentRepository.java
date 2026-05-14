package com.safeguardsa.repositories;

import com.safeguardsa.models.MedicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author ntsak
 */
@Repository
public interface MedicalDocumentRepository extends JpaRepository<MedicalDocument, Long> {

    // --- Basic search by keyword in content (used in dev / H2) ---
    @Query("SELECT d FROM MedicalDocument d WHERE LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MedicalDocument> findByContentContaining(@Param("keyword") String keyword);

    // --- Search by title ---
    List<MedicalDocument> findByTitleContainingIgnoreCase(String title);

    // --- Search by source file ---
    List<MedicalDocument> findBySource(String source);

    // --- Fetch a limited number of documents (for RAG context window) ---
    @Query("SELECT d FROM MedicalDocument d ORDER BY d.id ASC")
    List<MedicalDocument> findTopN(@Param("limit") int limit);

    // --- Count total documents loaded ---
    long count();

}

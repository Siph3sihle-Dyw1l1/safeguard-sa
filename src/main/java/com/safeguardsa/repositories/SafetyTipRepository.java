

package com.safeguardsa.repositories;

import com.safeguardsa.models.SafetyTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
/**
 *
 * @author ntsak
 */
@Repository
public interface SafetyTipRepository extends JpaRepository<SafetyTip, Long> {

    // --- Basic filters ---
    List<SafetyTip> findByProvince(String province);
    List<SafetyTip> findByCategory(String category);
    List<SafetyTip> findByStatus(String status);
    List<SafetyTip> findByProvinceAndStatus(String province, String status);
    List<SafetyTip> findByCategoryAndStatus(String category, String status);

    // --- Map: only approved tips with coordinates ---
    @Query("SELECT t FROM SafetyTip t WHERE t.status = 'APPROVED' AND t.latitude IS NOT NULL AND t.longitude IS NOT NULL")
    List<SafetyTip> findApprovedWithCoordinates();

    // --- Dashboard: count approved tips grouped by category ---
    @Query("SELECT t.category, COUNT(t) FROM SafetyTip t WHERE t.status = 'APPROVED' GROUP BY t.category")
    List<Object[]> countByCategoryGrouped();

    // --- Dashboard: count approved tips grouped by province ---
    @Query("SELECT t.province, COUNT(t) FROM SafetyTip t WHERE t.status = 'APPROVED' GROUP BY t.province")
    List<Object[]> countByProvinceGrouped();

    // --- Dashboard: monthly trend (count approved tips per month) ---
    @Query("SELECT MONTH(t.submittedAt), COUNT(t) FROM SafetyTip t WHERE t.status = 'APPROVED' AND t.submittedAt >= :from GROUP BY MONTH(t.submittedAt) ORDER BY MONTH(t.submittedAt)")
    List<Object[]> countByMonthSince(@Param("from") LocalDateTime from);

    // --- Admin: tips submitted within a date range ---
    @Query("SELECT t FROM SafetyTip t WHERE t.submittedAt BETWEEN :start AND :end ORDER BY t.submittedAt DESC")
    List<SafetyTip> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // --- Admin: count pending tips ---
    long countByStatus(String status);
}

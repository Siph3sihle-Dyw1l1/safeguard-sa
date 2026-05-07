package com.safeguardsa.repositories;

import com.safeguardsa.models.SafetyTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SafetyTipRepository extends JpaRepository<SafetyTip, Long> {

    List<SafetyTip> findByProvince(String province);

    List<SafetyTip> findByCategory(String category);

    List<SafetyTip> findByStatus(String status);

    // FIX: Added for Leaflet.js map integration 
    @Query("SELECT t FROM SafetyTip t WHERE t.status = 'APPROVED' AND t.latitude IS NOT NULL AND t.longitude IS NOT NULL")
    List<SafetyTip> findApprovedWithCoordinates();

    @Query("SELECT t.category, COUNT(t) FROM SafetyTip t WHERE t.status = 'APPROVED' GROUP BY t.category")
    List<Object[]> countByCategoryGrouped();

    @Query("SELECT t.province, COUNT(t) FROM SafetyTip t WHERE t.status = 'APPROVED' GROUP BY t.province")
    List<Object[]> countByProvinceGrouped();

    @Query("SELECT MONTH(t.submittedAt), COUNT(t) FROM SafetyTip t WHERE t.status = 'APPROVED' AND t.submittedAt >= :from GROUP BY MONTH(t.submittedAt) ORDER BY MONTH(t.submittedAt)")
    List<Object[]> countByMonthSince(@Param("from") LocalDateTime from);

    long countByStatus(String status);
}

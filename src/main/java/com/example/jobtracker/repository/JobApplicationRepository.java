package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationStatus;
import com.example.jobtracker.entity.JobApplication;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByStatus(ApplicationStatus status);

    List<JobApplication> findByStatusOrderByAppliedDateDesc(ApplicationStatus status);

    boolean existsByJobHash(String jobHash);

    @Query("""
            SELECT application
            FROM JobApplication application
            WHERE (:status IS NULL OR application.status = :status)
              AND (:company IS NULL OR LOWER(application.companyName) LIKE LOWER(CONCAT('%', :company, '%')))
              AND (:fromDate IS NULL OR application.appliedDate >= :fromDate)
              AND (:toDate IS NULL OR application.appliedDate <= :toDate)
              AND (:keyword IS NULL
                   OR LOWER(application.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(application.role) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<JobApplication> searchApplications(@Param("status") ApplicationStatus status,
            @Param("company") String company,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
            SELECT application.status AS status, COUNT(application.id) AS count
            FROM JobApplication application
            GROUP BY application.status
            """)
    List<StatusCountProjection> countApplicationsPerStatus();

    long countByStatus(ApplicationStatus status);

    @Query("""
            SELECT application.appliedDate AS date, COUNT(application.id) AS count
            FROM JobApplication application
            WHERE application.appliedDate >= :fromDate
            GROUP BY application.appliedDate
            ORDER BY application.appliedDate ASC
            """)
    List<WeeklyActivityProjection> countWeeklyActivity(@Param("fromDate") LocalDate fromDate);
}

package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationAutomationLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationAutomationLogRepository extends JpaRepository<ApplicationAutomationLog, Long> {

    List<ApplicationAutomationLog> findByJobApplicationIdOrderByCreatedAtDesc(Long jobApplicationId);
}

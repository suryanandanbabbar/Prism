package com.example.jobtracker.repository;

import com.example.jobtracker.entity.JobApplicationStatusHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationStatusHistoryRepository extends JpaRepository<JobApplicationStatusHistory, Long> {

    List<JobApplicationStatusHistory> findByJobApplicationIdOrderByChangedAtDesc(Long jobApplicationId);
}

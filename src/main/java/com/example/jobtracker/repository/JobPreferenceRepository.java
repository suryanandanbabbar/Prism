package com.example.jobtracker.repository;

import com.example.jobtracker.entity.JobPreference;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPreferenceRepository extends JpaRepository<JobPreference, Long> {

    List<JobPreference> findByUserId(Long userId);
}

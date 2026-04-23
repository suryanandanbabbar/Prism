package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ResumeVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeVersionRepository extends JpaRepository<ResumeVersion, Long> {

    List<ResumeVersion> findByCvDocumentIdOrderByVersionNumberDesc(Long cvDocumentId);

    Optional<ResumeVersion> findTopByCvDocumentIdOrderByVersionNumberDesc(Long cvDocumentId);

    Optional<ResumeVersion> findByCvDocumentIdAndVersionNumber(Long cvDocumentId, Integer versionNumber);
}

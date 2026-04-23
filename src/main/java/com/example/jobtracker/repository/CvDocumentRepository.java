package com.example.jobtracker.repository;

import com.example.jobtracker.entity.CvDocument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CvDocumentRepository extends JpaRepository<CvDocument, Long> {

    List<CvDocument> findByUserId(Long userId);
}

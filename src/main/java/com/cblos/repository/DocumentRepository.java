package com.cblos.repository;

import com.cblos.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByLoanApplication_ApplicationId(Integer applicationId);
}
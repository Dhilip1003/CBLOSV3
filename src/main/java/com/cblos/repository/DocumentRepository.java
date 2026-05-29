package com.cblos.repository;

import com.cblos.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    
    // Used by Loan Officers to find files for a specific Loan Application
    List<Document> findByLoanApplication_ApplicationId(Integer applicationId);
    
    // ─── NEW: Used by Loan Officers to verify legal KYB papers for a Corporate Customer ───
    List<Document> findByCorporateCustomer_Id(Integer customerId);
}
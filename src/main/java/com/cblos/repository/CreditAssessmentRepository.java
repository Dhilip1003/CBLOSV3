package com.cblos.repository;

import com.cblos.model.CreditAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CreditAssessmentRepository extends JpaRepository<CreditAssessment, Integer> {
    // Find the credit assessment for a specific loan application
    Optional<CreditAssessment> findByLoanApplication_ApplicationId(Integer applicationId);
}
package com.cblos.repository;

import com.cblos.model.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Integer> {
    // Find all approvals for a specific application
    List<Approval> findByLoanApplication_ApplicationId(Integer applicationId);
    
    // Find all approvals done by a specific loan officer
    List<Approval> findByApprovedBy_Id(Integer officerId);
}
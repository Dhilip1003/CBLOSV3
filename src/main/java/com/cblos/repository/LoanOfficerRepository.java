package com.cblos.repository;

import com.cblos.model.LoanOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LoanOfficerRepository extends JpaRepository<LoanOfficer, Integer> {
    Optional<LoanOfficer> findByEmployeeId(String employeeId);
    
 // NEW: Smart routing query to find the officer with the lowest active workload
    @Query(value = "SELECT o.* FROM loan_officer o " +
                   "LEFT JOIN loan_application a ON o.id = a.officer_id AND a.status IN ('PENDING', 'UNDER_REVIEW') " +
                   "GROUP BY o.id " +
                   "ORDER BY COUNT(a.application_id) ASC LIMIT 1", 
           nativeQuery = true)
    Optional<LoanOfficer> findLeastLoadedOfficer();
}
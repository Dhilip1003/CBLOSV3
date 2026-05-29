package com.cblos.repository;

import com.cblos.model.LoanOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LoanOfficerRepository extends JpaRepository<LoanOfficer, Integer> {
    
    Optional<LoanOfficer> findByEmployeeId(String employeeId);
    
    /**
     * 🧠 FIXED: Finds the single active internal Officer with the lowest application counter
     */
    @Query(value = "SELECT * FROM loan_officer WHERE role = 'OFFICER' ORDER BY active_application_count ASC LIMIT 1", nativeQuery = true)
    Optional<LoanOfficer> findLeastLoadedOfficer();
}
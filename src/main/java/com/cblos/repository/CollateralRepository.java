package com.cblos.repository;

import com.cblos.model.Collateral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CollateralRepository extends JpaRepository<Collateral, Integer> {
    // Find all collateral tied to a specific loan application
    List<Collateral> findByLoanApplication_ApplicationId(Integer applicationId);
}
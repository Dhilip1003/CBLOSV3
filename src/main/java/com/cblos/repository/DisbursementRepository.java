package com.cblos.repository;

import com.cblos.model.Disbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DisbursementRepository extends JpaRepository<Disbursement, Integer> {
    // Find all disbursements tied to a specific active loan account
    List<Disbursement> findByLoanAccount_Id(Integer accountId);
}
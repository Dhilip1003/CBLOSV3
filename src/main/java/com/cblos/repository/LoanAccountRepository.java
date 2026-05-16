package com.cblos.repository;

import com.cblos.model.LoanAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LoanAccountRepository extends JpaRepository<LoanAccount, Integer> {
    // Find an account by its unique bank account number
    Optional<LoanAccount> findByAccountNumber(String accountNumber);
    
    // Find the generated account for a specific loan application
    Optional<LoanAccount> findByLoanApplication_ApplicationId(Integer applicationId);
}
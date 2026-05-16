package com.cblos.service;

import com.cblos.model.Collateral;
import com.cblos.model.LoanApplication;
import com.cblos.repository.CollateralRepository;
import com.cblos.repository.LoanApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CollateralService {

    @Autowired
    private CollateralRepository collateralRepository;

    @Autowired
    private LoanApplicationRepository loanRepository;

    public Collateral addCollateralToApplication(Integer applicationId, Collateral collateral) {
        LoanApplication app = loanRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Loan application not found"));
        
        collateral.setLoanApplication(app);
        return collateralRepository.save(collateral);
    }

    public List<Collateral> getCollateralForApplication(Integer applicationId) {
        return collateralRepository.findByLoanApplication_ApplicationId(applicationId);
    }
}
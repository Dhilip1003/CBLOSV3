package com.cblos.service;

import com.cblos.model.Collateral;
import com.cblos.model.LoanApplication;
import com.cblos.repository.CollateralRepository;
import com.cblos.repository.LoanApplicationRepository;
import com.cblos.security.AccessControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CollateralService {

    @Autowired
    private CollateralRepository collateralRepository;

    @Autowired
    private LoanApplicationRepository loanRepository;

    @Autowired
    private AccessControlService accessControl;

    public Collateral addCollateralToApplication(Integer applicationId, Collateral collateral) {
        accessControl.ensureCustomerOwnsApplication(applicationId);
        
        // 1. Find the target loan application row context
        LoanApplication app = loanRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Loan application not found with ID: " + applicationId));
        
        if (collateral.getEstimatedValue() == null || collateral.getEstimatedValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Validation Failed: Collateral valuation must be a positive asset amount.");
        }

        if (collateral.getAssetReferenceNumber() == null || collateral.getAssetReferenceNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Failed: Legal Asset Registration/Reference number is mandatory.");
        }

        // 2. Map default initialization security parameters
        collateral.setLoanApplication(app);
        collateral.setVerificationStatus("PENDING"); // ◄── Set default status until verified by field team

        // Save this individual asset record to the database first
        Collateral savedCollateral = collateralRepository.save(collateral);

        // 3. ─── 📊 RUN REAL-TIME COLLATERAL COVERAGE VERIFICATION MATRIX ───
        // Sum up the value of ALL assets pledged to this specific loan application so far
        List<Collateral> allPledgedAssets = collateralRepository.findByLoanApplication_ApplicationId(applicationId);
        
        BigDecimal totalCollateralValue = allPledgedAssets.stream()
                .map(Collateral::getEstimatedValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal requestedLoanAmount = app.getLoanAmount();

        // Calculate Coverage Margin Requirement (e.g., Collateral must be at least 1.2x (120%) of the loan amount)
        BigDecimal minimumRequiredCollateral = requestedLoanAmount.multiply(BigDecimal.valueOf(1.20));

        System.out.println("🔍 [CB-LOS Risk Engine] Loan Amount: ₹" + requestedLoanAmount 
                + " | Current Cumulative Pledged Asset Value: ₹" + totalCollateralValue);

        if (totalCollateralValue.compareTo(minimumRequiredCollateral) < 0) {
            // Note: We don't throw an exception here because the user might be planning to add multiple assets 
            // sequentially on this page. We log an internal alert string status instead.
            System.out.println("⚠️ Risk Alert: Pledged assets do not meet the bank's 120% security coverage threshold yet.");
        } else {
            System.out.println("🟢 Risk Safe: Collateral coverage ratio successfully clears the bank's safety threshold.");
        }

        return savedCollateral;
    }

    public List<Collateral> getCollateralForApplication(Integer applicationId) {
        accessControl.ensureCustomerOwnsApplication(applicationId);
        return collateralRepository.findByLoanApplication_ApplicationId(applicationId);
    }

    /**
     * ─── NEW METHOD: Loan Officer Verification Flow ───
     * Executed later when a bank employee physically inspects the assets.
     */
    public Collateral verifyCollateralAsset(Integer collateralId, boolean approve) {
        Collateral collateral = collateralRepository.findById(collateralId)
                .orElseThrow(() -> new RuntimeException("Collateral record missing with ID: " + collateralId));
        
        collateral.setVerificationStatus(approve ? "VERIFIED" : "REJECTED");
        return collateralRepository.save(collateral);
    }
}
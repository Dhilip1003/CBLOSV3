package com.cblos.service;

import com.cblos.model.LoanApplication;
import com.cblos.model.LoanOfficer;
import com.cblos.model.CorporateCustomer;
import com.cblos.model.LoanAccount;
import com.cblos.model.LoanProduct;
import com.cblos.repository.LoanApplicationRepository;
import com.cblos.repository.LoanOfficerRepository;
import com.cblos.repository.CorporateCustomerRepository;
import com.cblos.repository.LoanAccountRepository;
import com.cblos.repository.LoanProductRepository;
import com.cblos.security.AccessControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanApplicationService {

    @Autowired
    private LoanApplicationRepository loanRepository;

    @Autowired
    private CorporateCustomerRepository customerRepository;
    
    @Autowired
    private LoanOfficerRepository officerRepository;

    @Autowired
    private LoanProductRepository productRepository; // ─── NEW: Added connection to read rule cards

    @Autowired
    private AccessControlService accessControl;
    
    @Autowired
    private LoanAccountRepository loanAccountRepository;

    @Autowired
    private DisbursementService disbursementService;

	    public LoanApplication submitApplication(LoanApplication app, Integer customerId) {
	        accessControl.ensureCustomerIdMatches(customerId);
	        
	        // 1. Verify and link corporate customer
	        CorporateCustomer customer = customerRepository.findById(customerId)
	                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
	        app.setCustomer(customer);
	        app.setSubmissionDate(LocalDate.now()); 
	
	        // 2. ─── NEW DYNAMIC VALIDATION MATRIX ENGINE ───
	        if (app.getLoanProduct() == null || app.getLoanProduct().getId() == null) {
	            throw new IllegalArgumentException("Validation Failed: You must select a valid credit product from the catalog.");
	        }
	
	        // Fetch the fresh rules assigned to this specific product from our database table
	        LoanProduct productRules = productRepository.findById(app.getLoanProduct().getId())
	                .orElseThrow(() -> new RuntimeException("Selected Credit Product option not found in database."));
	        
	        // Sync the textual loan type label to match the product catalog name automatically
	        app.setLoanType(productRules.getProductName());
	
	        BigDecimal amount = app.getLoanAmount();
	        Integer tenure = app.getRequestedTenureMonths();
	
	        if (amount == null || tenure == null) {
	            throw new IllegalArgumentException("Validation Failed: Requested loan amount and tenure timeline cannot be blank.");
	        }
	
	        // Rule A: Validate physical Tenure Boundaries 
	        if (tenure < productRules.getMinTenureMonths() || tenure > productRules.getMaxTenureMonths()) {
	            throw new IllegalArgumentException("Validation Failed: For " + productRules.getProductName() 
	                    + ", selected payback window must be between " + productRules.getMinTenureMonths() 
	                    + " and " + productRules.getMaxTenureMonths() + " months.");
	        }
	
	        // Rule B: Validate Standard Physical Budget Boundaries
	        BigDecimal minLimit = BigDecimal.valueOf(productRules.getMinLoanAmount());
	        BigDecimal maxLimit = BigDecimal.valueOf(productRules.getMaxLoanAmount());
	
	        if (amount.compareTo(minLimit) < 0 || amount.compareTo(maxLimit) > 0) {
	            throw new IllegalArgumentException("Validation Failed: For " + productRules.getProductName() 
	                    + ", requested amount must fall between ₹" + productRules.getMinLoanAmount() 
	                    + " and ₹" + productRules.getMaxLoanAmount());
	        }
	
	        // 📊 Rule C: YOUR CUSTOM TIME-RISK ACCELERATOR (The Advanced Risk-Scaling Feature)
	        // If a customer changes tenure to a long window (> 36 months), we scale down their maximum limit 
	        // to protect the bank's liquidity from risk.
	        if (tenure > 36) {
	            BigDecimal restrictedMaxLimit = maxLimit.multiply(BigDecimal.valueOf(0.70)); // Cuts exposure ceiling by 30%
	            if (amount.compareTo(restrictedMaxLimit) > 0) {
	                throw new IllegalArgumentException("Risk Guardrail: For extended terms exceeding 36 months, the maximum capital exposure allowed for this product is scaled down to ₹" + restrictedMaxLimit);
	            }
	        }
	
	     // 3. Intelligent Workload Routing (Updates the counter in real-time!)
	        LoanOfficer availableOfficer = officerRepository.findLeastLoadedOfficer()
	                .orElse(null); 
	
	        if (availableOfficer != null) {
	            app.setLoanOfficer(availableOfficer);
	            app.setStatus("UNDER_REVIEW"); 
	            
	            // ─── 📊 ADD THIS BLOCK TO UPDATE THE ONBOARDED WORKER'S COUNTER ───
	            availableOfficer.setActiveApplicationCount(availableOfficer.getActiveApplicationCount() + 1);
	            officerRepository.save(availableOfficer); // Saves the updated counter to the database
	            
	            System.out.println("🟢 [LOS Router] Allocated App to: " + availableOfficer.getName() 
	                    + " | New Workload: " + availableOfficer.getActiveApplicationCount());
	        } else {
	            app.setStatus("PENDING"); 
	        }
	        
	        return loanRepository.save(app);
	    }

    public String getStatusById(Integer id) {
        accessControl.ensureCustomerOwnsApplication(id);
        return loanRepository.findById(id)
                .map(LoanApplication::getStatus)
                .orElse("Application Not Found");
    }

    public LoanApplication getApplicationById(Integer id) {
        accessControl.ensureCustomerOwnsApplication(id);
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan Application Not Found"));
    }
    
    public List<LoanApplication> getAllApplications() {
        if (accessControl.isCustomer()) {
            Integer companyId = accessControl.currentUser().getCorporateCustomerId();
            return loanRepository.findByCustomer_Id(companyId); // Keeps your original customer security lookup filter
        }
        return loanRepository.findAll();
    }
    
    
    
    /**
     * 🧑‍💼 STEP 1: LOAN OFFICER EVALUATION & CREDIT ASSESSMENT
     * The assigned officer reviews the customer's data/collateral and inputs a credit risk score.
     */
    public LoanApplication officerEvaluateApplication(Integer applicationId, Integer creditScore, String assessmentNotes, boolean passToManager) {
        
        // 1. Fetch the active application from the database
        LoanApplication app = loanRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Loan Application Not Found with ID: " + applicationId));

        // 2. Security Guardrail: Ensure the loan is actually waiting for an officer review
        if (!"UNDER_REVIEW".equalsIgnoreCase(app.getStatus())) {
            throw new IllegalStateException("Operation Denied: This application is not currently under active officer review.");
        }

        // 3. Automated Risk Rule Matrix Calculation
        // If the credit score checked from their business documents is too low, reject immediately!
        if (creditScore < 600) {
            app.setStatus("REJECTED");
            
            // Decouple the officer's workload counter since this case is now closed
            LoanOfficer assignedOfficer = app.getLoanOfficer();
            if (assignedOfficer != null && assignedOfficer.getActiveApplicationCount() > 0) {
                assignedOfficer.setActiveApplicationCount(assignedOfficer.getActiveApplicationCount() - 1);
                officerRepository.save(assignedOfficer);
            }
            
            System.out.println("❌ Risk Engine Alert: Application automatically rejected due to poor credit score: " + creditScore);
            return loanRepository.save(app);
        }

        // 4. Route forward based on the Officer's audit decision
        if (passToManager) {
            app.setStatus("PENDING_MANAGER_APPROVAL"); // ◄── Passes the file up to the Manager's desk!
            System.out.println("💼 Officer Audit Complete: Forwarding Application ID " + applicationId + " to Manager queue.");
        } else {
            app.setStatus("REJECTED");
            
            // Clear workload counter on rejection
            LoanOfficer assignedOfficer = app.getLoanOfficer();
            if (assignedOfficer != null && assignedOfficer.getActiveApplicationCount() > 0) {
                assignedOfficer.setActiveApplicationCount(assignedOfficer.getActiveApplicationCount() - 1);
                officerRepository.save(assignedOfficer);
            }
        }

        return loanRepository.save(app);
    }

    /**
     * 🏢 STEP 2: MANAGER FINAL AUTHORIZATION WITH SERVICING ACTIVATION
     */
    public LoanApplication managerFinalDecision(Integer applicationId, boolean finalApprove) {
        
        LoanApplication app = loanRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Loan Application Not Found with ID: " + applicationId));

        if (!"PENDING_MANAGER_APPROVAL".equalsIgnoreCase(app.getStatus())) {
            throw new IllegalStateException("Operation Denied: This application requires an Officer's recommendation before a Manager can act.");
        }

        if (finalApprove) {
            app.setStatus("APPROVED"); 
            System.out.println("Model State Updated: Application ID " + applicationId + " is officially APPROVED.");

            // ─── 🚀 THE AUTOMATION STEP: GENERATE REAL-WORLD LOAN ACCOUNT ───
            LoanAccount newAccount = new LoanAccount();
            newAccount.setLoanApplication(app);
            newAccount.setCustomer(app.getCustomer());
            newAccount.setPrincipalAmount(app.getLoanAmount());
            newAccount.setOpeningDate(LocalDate.now());
            newAccount.setStatus("Active");
            
            // Build a clean, trackable routing account string
            String systemGeneratedNo = "ACT-" + System.currentTimeMillis();
            newAccount.setAccountNumber(systemGeneratedNo);

            // ─── 📊 NEW: DYNAMIC INTEREST RATE RISK MATRIX ───
            Integer requestedTenure = app.getRequestedTenureMonths();
            Double calculatedRate;

            if (requestedTenure == null || requestedTenure <= 12) {
                calculatedRate = 10.50; // Low Risk / Short Term
            } else if (requestedTenure <= 36) {
                calculatedRate = 12.00; // Standard Corporate Term
            } else {
                calculatedRate = 14.50; // High Risk / Extended Term Ceilings
            }

            newAccount.setInterestRate(calculatedRate); // ◄── Saved dynamically based on customer tenure!
            System.out.println("📈 Dynamic Pricing Applied: Tenure is " + requestedTenure + " months. Rate set to " + calculatedRate + "%");

            // Save the newly activated banking asset
            LoanAccount savedAccount = loanAccountRepository.save(newAccount);
            System.out.println("🏛️ Core Ledger Updated: Loan Account " + systemGeneratedNo + " generated.");

            // ─── 🔁 HAND OFF ASSET TO DISBURSEMENT ENGINE ───
            disbursementService.scheduleDisbursement(savedAccount);

        } else {
            app.setStatus("REJECTED");
        }

        // Adjust assigned worker load metrics
        LoanOfficer assignedOfficer = app.getLoanOfficer();
        if (assignedOfficer != null && assignedOfficer.getActiveApplicationCount() > 0) {
            assignedOfficer.setActiveApplicationCount(assignedOfficer.getActiveApplicationCount() - 1);
            officerRepository.save(assignedOfficer);
        }

        return loanRepository.save(app);
    }
}
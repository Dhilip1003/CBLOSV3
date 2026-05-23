package com.cblos.service;

import com.cblos.model.*;
import com.cblos.repository.*;
import com.cblos.security.AccessControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ApprovalService {

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private LoanApplicationRepository loanRepository;
    
    @Autowired
    private LoanOfficerRepository officerRepository;
    
    @Autowired
    private LoanAccountRepository accountRepository;

    @Autowired
    private DisbursementService disbursementService;
    
    @Autowired
    private CreditAssessmentRepository assessmentRepository;

    @Autowired
    private CollateralRepository collateralRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private AccessControlService accessControl;

    public Approval submitApproval(Integer applicationId, Integer approverId, Integer level, String status, String comments) {
        
        // 1. Fetch data
        LoanApplication application = loanRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Loan application not found"));

        LoanOfficer officer = officerRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Loan Officer not found"));

        // 2. SECURITY GATE: Only validate requirements if the action is "Approved"
        if ("Approved".equalsIgnoreCase(status)) {
            
            // A. Document Validation
            List<Document> docs = documentRepository.findByLoanApplication_ApplicationId(applicationId);
            // Check if documents exist and at least one is validated as 'isValid'
            boolean hasValidDocs = docs.stream().anyMatch(d -> d.getIsValid() != null && d.getIsValid());

            if (docs.isEmpty() || !hasValidDocs) {
                throw new RuntimeException("Validation Failed: No validated documents found for this application.");
            }

            // B. Credit Assessment Validation
            CreditAssessment assessment = assessmentRepository.findByLoanApplication_ApplicationId(applicationId)
                    .orElseThrow(() -> new RuntimeException("Validation Failed: Cannot approve without a Credit Assessment."));

            // C. Collateral Validation
            List<Collateral> collaterals = collateralRepository.findByLoanApplication_ApplicationId(applicationId);
            if (collaterals.isEmpty()) {
                throw new RuntimeException("Validation Failed: Cannot approve without pledged Collateral.");
            }

            // D. Risk Score Threshold
            if (assessment.getRiskScore() != null && assessment.getRiskScore() > 10.0) {
                throw new RuntimeException("Validation Failed: Risk score too high for automatic approval.");
            }
        }

        // 3. Update Application Status
        if ("Rejected".equalsIgnoreCase(status)) {
            application.setStatus("REJECTED");
        } else if (level >= 2 && "Approved".equalsIgnoreCase(status)) {
            application.setStatus("APPROVED");
        } else {
            application.setStatus("UNDER_REVIEW");
        }
        
        loanRepository.save(application);

        // 4. Create Audit Record
        Approval approval = new Approval();
        approval.setLoanApplication(application);
        approval.setApprovedBy(officer);
        approval.setApprovalLevel(level);
        approval.setApprovalStatus(status);
        approval.setComments(comments);
        approval.setApprovalDate(LocalDateTime.now());

        return approvalRepository.save(approval);
    }

    @Transactional
    public void processApproval(Integer id, String status, String comments) {
        LoanApplication application = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan application not found"));

        String currentStatus = application.getStatus();
        if (currentStatus != null) {
            String normalized = currentStatus.trim().toUpperCase();
            if ("APPROVED".equals(normalized) || "REJECTED".equals(normalized)) {
                throw new IllegalStateException(
                        "Loan application is already " + normalized + ". Cannot process again.");
            }
        }

        Integer approverId = accessControl.getCurrentOfficerId();
        Approval result = submitApproval(id, approverId, 2, status, comments);
        
        if ("Approved".equalsIgnoreCase(status)) {
            LoanApplication app = result.getLoanApplication();
            
            LoanAccount account = new LoanAccount();
            account.setCustomer(app.getCustomer());
            account.setLoanApplication(app);
            account.setAccountNumber("ACCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            account.setPrincipalAmount(app.getLoanAmount());
            account.setStatus("Active");
            account.setOpeningDate(LocalDate.now());
            account.setInterestRate(5.5); 
            
            LoanAccount savedAccount = accountRepository.save(account);
            disbursementService.scheduleDisbursement(savedAccount);
        }
    }

    public List<Approval> getApprovalHistory(Integer applicationId) {
        return approvalRepository.findByLoanApplication_ApplicationId(applicationId);
    }
}
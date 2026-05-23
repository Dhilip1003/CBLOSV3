package com.cblos.service;

import com.cblos.model.LoanApplication;
import com.cblos.model.LoanOfficer;
import com.cblos.model.CorporateCustomer;
import com.cblos.repository.LoanApplicationRepository;
import com.cblos.repository.LoanOfficerRepository;
import com.cblos.repository.CorporateCustomerRepository;
import com.cblos.security.AccessControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private AccessControlService accessControl;

    public LoanApplication submitApplication(LoanApplication app, Integer customerId) {
        accessControl.ensureCustomerIdMatches(customerId);
        // 1. Find the customer
        CorporateCustomer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        // 2. Link customer
        app.setCustomer(customer);
        app.setSubmissionDate(LocalDate.now()); 
        
        // 3. NEW LOGIC: Intelligent Workload Routing
        // Find the officer with the fewest tasks and assign them instantly
        LoanOfficer availableOfficer = officerRepository.findLeastLoadedOfficer()
                .orElse(null); // If no officers exist in the DB, it remains null

        if (availableOfficer != null) {
            app.setLoanOfficer(availableOfficer);
            app.setStatus("UNDER_REVIEW"); // Jumps straight to review because an officer has it
        } else {
            app.setStatus("PENDING"); // No officers available, sits in the queue
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
            return loanRepository.findByCustomer_Id(companyId);
        }
        return loanRepository.findAll();
    }
}
package com.cblos.service;

import com.cblos.model.CorporateCustomer;
import com.cblos.model.Document;
import com.cblos.model.LoanOfficer; // ◄── Linked your existing Staff model
import com.cblos.repository.CorporateCustomerRepository;
import com.cblos.repository.DocumentRepository;
import com.cblos.repository.LoanOfficerRepository; // ◄── Injecting to save internal workers
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private CorporateCustomerRepository customerRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private LoanOfficerRepository officerRepository; // ◄── Added this link

    /**
     * 🔍 1. Fetch all corporate accounts waiting for admin vetting
     */
    public List<CorporateCustomer> getPendingRegistrations() {
        return customerRepository.findAll().stream()
                .filter(c -> "PENDING_VERIFICATION".equalsIgnoreCase(c.getStatus()))
                .toList();
    }

    /**
     * 📂 2. Fetch the KYB legal papers uploaded by this specific customer
     */
    public List<Document> getCustomerRegistrationDocuments(Integer customerId) {
        return documentRepository.findByCorporateCustomer_Id(customerId);
    }

    /**
     * 🚀 3. Process the Final Admin Sign-Off (Approve / Reject)
     */
    public CorporateCustomer reviewCustomerRegistration(Integer customerId, boolean approve, String reason) {
        CorporateCustomer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Corporate profile not found with ID: " + customerId));

        if (approve) {
            customer.setLegalDocumentStatus("VERIFIED_LEGITIMATE");
            customer.setStatus("ACTIVE");
            customer.setRejectionReason(null);
        } else {
            customer.setLegalDocumentStatus("REJECTED");
            customer.setStatus("REJECTED");
            customer.setRejectionReason(reason != null ? reason : "Legal documents could not be verified by the admin.");
        }

        return customerRepository.save(customer);
    }

    /**
     * 🧑‍💼 4. FIXED WORKFLOW: ONBOARD INTERNAL BANK STAFF
     */
    public LoanOfficer onboardBankStaff(LoanOfficer newStaff) {
        if (newStaff.getEmployeeId() == null || newStaff.getEmployeeId().trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Failed: Employee ID code cannot be blank.");
        }
        
        // 💡 Check your LoanOfficer.java model for these exact field names.
        // If your field is called "role", these will compile cleanly!
        if (newStaff.getRole() == null) {
            newStaff.setRole("OFFICER"); 
        }
        
        // If your counter field in LoanOfficer.java has a different name, 
        // match it here (e.g., setLoanCount(0) or setActiveLoans(0))
        newStaff.setActiveApplicationCount(0); 

        return officerRepository.save(newStaff);
    }
}	
package com.cblos.service;

import com.cblos.model.CorporateCustomer;
import com.cblos.model.AppUser;
import com.cblos.repository.CorporateCustomerRepository;
import com.cblos.repository.AppUserRepository;
import com.cblos.security.AccessControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CorporateCustomerService {

    @Autowired
    private CorporateCustomerRepository customerRepository;

    @Autowired
    private AppUserRepository userRepository; // ─── NEW: Connected to manage credential locks

    @Autowired
    private AccessControlService accessControl;

    /**
     * Public Registration Entry Point: Onboards a brand new company profile.
     */
    public CorporateCustomer onboardCustomer(CorporateCustomer customer) {
        if (customerRepository.findByTaxId(customer.getTaxId()).isPresent()) {
            throw new RuntimeException("Validation Failed: Customer with this Tax ID already exists.");
        }
        
        customer.setStatus("PENDING_VERIFICATION");
        customer.setLegalDocumentStatus("UNVERIFIED");
        customer.setRejectionReason(null);
        
        return customerRepository.save(customer);
    }

    /**
     * ✏️ CUSTOMER CORRECTION GATE (PATCH mapping destination)
     */
    public CorporateCustomer updatePendingCustomerDetails(Integer id, CorporateCustomer updatedData) {
        CorporateCustomer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Corporate Customer profile not found"));

        String currentStatus = existingCustomer.getStatus();
        
        if (!"PENDING_VERIFICATION".equals(currentStatus) && !"REJECTED_INVALID_DOCUMENTS".equals(currentStatus)) {
            throw new IllegalStateException("Profile modification locked: This profile has already been approved and cannot be edited directly.");
        }

        if (updatedData.getCompanyName() != null) existingCustomer.setCompanyName(updatedData.getCompanyName());
        if (updatedData.getPhoneNumber() != null) existingCustomer.setPhoneNumber(updatedData.getPhoneNumber());
        if (updatedData.getBusinessAddress() != null) existingCustomer.setBusinessAddress(updatedData.getBusinessAddress());
        if (updatedData.getIndustryType() != null) existingCustomer.setIndustryType(updatedData.getIndustryType());
        if (updatedData.getAnnualRevenue() != null) existingCustomer.setAnnualRevenue(updatedData.getAnnualRevenue());
        if (updatedData.getYearsInBusiness() != null) existingCustomer.setYearsInBusiness(updatedData.getYearsInBusiness());
        if (updatedData.getTaxId() != null) existingCustomer.setTaxId(updatedData.getTaxId());
        if (updatedData.getRegistrationNumber() != null) existingCustomer.setRegistrationNumber(updatedData.getRegistrationNumber());

        if ("REJECTED_INVALID_DOCUMENTS".equals(currentStatus)) {
            existingCustomer.setStatus("PENDING_VERIFICATION");
            existingCustomer.setLegalDocumentStatus("UNVERIFIED");
            existingCustomer.setRejectionReason(null); 
        }

        return customerRepository.save(existingCustomer);
    }

    /**
     * 🏛️ BANK STAFF / ADMIN VERIFICATION GATE
     * Fixed to automatically look up and toggle the corresponding AppUser login credentials!
     */
    @Transactional // Ensures both customer and login records update together perfectly or not at all
    public CorporateCustomer verifyCustomerLegitimacy(Integer customerId, String reviewStatus, String reason) {
        // A. Load customer record
        CorporateCustomer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Corporate Customer profile not found"));

        // B. Load matching credential record
        AppUser userAccount = userRepository.findByCorporateCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Associated user credential account not found for this customer ID"));

        if ("APPROVE".equalsIgnoreCase(reviewStatus)) {
            // Update customer profile parameters
            customer.setLegalDocumentStatus("VERIFIED_LEGITIMATE");
            customer.setStatus("ACTIVE"); 
            customer.setRejectionReason(null); 
            
            // ─── THE SECURITY UNLOCK ───
            userAccount.setActive(true); // Customer can now log into the live dashboard!
            userRepository.save(userAccount);

        } else if ("REJECT".equalsIgnoreCase(reviewStatus)) {
            customer.setLegalDocumentStatus("REJECTED");
            customer.setStatus("REJECTED_INVALID_DOCUMENTS"); 
            customer.setRejectionReason(reason); 
            
            // Keep their login locked to the Status Portal page
            userAccount.setActive(false); 
            userRepository.save(userAccount);
            
        } else {
            throw new IllegalArgumentException("Validation Failed: Invalid review status action string '" + reviewStatus + "'. Use APPROVE or REJECT.");
        }

        return customerRepository.save(customer);
    }

    public CorporateCustomer getCustomerById(Integer id) {
        accessControl.ensureCustomerOwnsCustomerRecord(id);
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Corporate Customer profile not found"));
    }

    public List<CorporateCustomer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
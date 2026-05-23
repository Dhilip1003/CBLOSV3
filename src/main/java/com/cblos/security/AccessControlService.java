package com.cblos.security;

import com.cblos.model.Document;
import com.cblos.model.LoanApplication;
import com.cblos.model.UserRole;
import com.cblos.repository.DocumentRepository;
import com.cblos.repository.LoanApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AccessControlService {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private DocumentRepository documentRepository;

    public AppUserDetails currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserDetails details)) {
            throw new AccessDeniedException("Not authenticated.");
        }
        return details;
    }

    public boolean isCustomer() {
        return currentUser().getRole() == UserRole.CUSTOMER;
    }

    public boolean isBankStaff() {
        UserRole role = currentUser().getRole();
        return role == UserRole.OFFICER || role == UserRole.MANAGER;
    }

    public void ensureCustomerIdMatches(Integer customerId) {
        if (!isCustomer()) {
            return;
        }
        Integer ownId = currentUser().getCorporateCustomerId();
        if (ownId == null || !ownId.equals(customerId)) {
            throw new AccessDeniedException("Access denied: you can only act for your own company.");
        }
    }

    public void ensureCustomerOwnsCustomerRecord(Integer customerId) {
        if (!isCustomer()) {
            return;
        }
        ensureCustomerIdMatches(customerId);
    }

    public void ensureCustomerOwnsApplication(Integer applicationId) {
        if (!isCustomer()) {
            return;
        }
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Loan application not found"));

        Integer ownId = currentUser().getCorporateCustomerId();
        if (ownId == null || application.getCustomer() == null
                || !ownId.equals(application.getCustomer().getId())) {
            throw new AccessDeniedException("Access denied: this loan application belongs to another company.");
        }
    }

    public void ensureCustomerOwnsDocument(Integer documentId) {
        if (!isCustomer()) {
            return;
        }
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        ensureCustomerOwnsApplication(document.getLoanApplication().getApplicationId());
    }

    public Integer getCurrentOfficerId() {
        if (!isBankStaff()) {
            throw new AccessDeniedException("Only bank staff can perform this action.");
        }
        Integer officerId = currentUser().getLoanOfficerId();
        if (officerId == null) {
            throw new AccessDeniedException("Bank user is not linked to a loan officer profile.");
        }
        return officerId;
    }
}

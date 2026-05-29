package com.cblos.controller;

import com.cblos.model.CorporateCustomer;
import com.cblos.model.Document;
import com.cblos.model.LoanOfficer;
import com.cblos.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 📊 1. Get the list of all pending customer profiles for the Admin Grid
    // GET http://localhost:8080/api/admin/customers/pending
    @GetMapping("/customers/pending")
    public ResponseEntity<List<CorporateCustomer>> listPendingCustomers() {
        return ResponseEntity.ok(adminService.getPendingRegistrations());
    }

    // 🗂️ 2. Get the legal document attachments for a specific pending customer
    // GET http://localhost:8080/api/admin/customers/{customerId}/documents
    @GetMapping("/customers/{customerId}/documents")
    public ResponseEntity<List<Document>> getCustomerDocs(@PathVariable Integer customerId) {
        return ResponseEntity.ok(adminService.getCustomerRegistrationDocuments(customerId));
    }

    // 🎯 3. Approve or Reject the registration profile
    // PUT http://localhost:8080/api/admin/customers/review/5?approve=true
    @PutMapping("/customers/review/{customerId}")
    public ResponseEntity<CorporateCustomer> verifyCustomer(
            @PathVariable Integer customerId,
            @RequestParam("approve") boolean approve,
            @RequestParam(value = "reason", required = false) String reason) {
        
        CorporateCustomer resolvedCustomer = adminService.reviewCustomerRegistration(customerId, approve, reason);
        return ResponseEntity.ok(resolvedCustomer);
    }
    
 // 🧑‍💼 Admin Endpoint to add an Officer or Manager
    // POST http://localhost:8080/api/admin/staff/onboard
    @PostMapping("/staff/onboard")
    public ResponseEntity<LoanOfficer> onboardStaff(@RequestBody LoanOfficer staffDetails) {
        LoanOfficer savedStaff = adminService.onboardBankStaff(staffDetails);
        return new ResponseEntity<>(savedStaff, org.springframework.http.HttpStatus.CREATED);
    }
}
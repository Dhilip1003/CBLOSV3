package com.cblos.controller;

import com.cblos.model.CorporateCustomer;
import com.cblos.service.CorporateCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CorporateCustomerController {

    @Autowired
    private CorporateCustomerService customerService;

    // 1. Onboard a new customer (Status defaults to PENDING_VERIFICATION)
    @PostMapping("/onboard")
    public ResponseEntity<CorporateCustomer> onboardCustomer(@RequestBody CorporateCustomer customer) {
        CorporateCustomer createdCustomer = customerService.onboardCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    // 2. ─── FIXED ENDPOINT: Added 'reason' parameter to match service signature ───
    // For Approvals: PUT /api/customers/verify/5?action=APPROVE
    // For Rejections: PUT /api/customers/verify/5?action=REJECT&reason=GSTIN number is unreadable
    @PutMapping("/verify/{id}")
    public ResponseEntity<CorporateCustomer> verifyCustomer(
            @PathVariable Integer id,
            @RequestParam("action") String action,
            @RequestParam(value = "reason", required = false) String reason) { // 'required = false' because approvals don't need a rejection reason
        
        // Pass all three arguments cleanly into your service logic layer
        CorporateCustomer verifiedCustomer = customerService.verifyCustomerLegitimacy(id, action, reason);
        return ResponseEntity.ok(verifiedCustomer);
    }

 // ─── FIXED TO ENTERPRISE STANDARD: Changed from PUT to PATCH ───
    // Allows the customer to modify ONLY the specific typos they made (Partial Update)
    // Example: PATCH /api/customers/update/5
    @PatchMapping("/update/{id}")
    public ResponseEntity<CorporateCustomer> updateCustomerDetails(
            @PathVariable Integer id,
            @RequestBody CorporateCustomer partialUpdatedData) {
        
        CorporateCustomer editedCustomer = customerService.updatePendingCustomerDetails(id, partialUpdatedData);
        return ResponseEntity.ok(editedCustomer);
    }

    // 4. Get a specific customer profile
    @GetMapping("/{id}")
    public ResponseEntity<CorporateCustomer> getCustomer(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    // 5. Get all customers (Used by internal bank staff to view onboarding queues)
    @GetMapping("/all")
    public ResponseEntity<List<CorporateCustomer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
}
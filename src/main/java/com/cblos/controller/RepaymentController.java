package com.cblos.controller;

import com.cblos.model.RepaymentSchedule;
import com.cblos.service.DisbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repayments")
public class RepaymentController {

    @Autowired
    private DisbursementService disbursementService;

    // 1. View the full installment plan for an account
    @GetMapping("/schedule/{accountId}")
    public ResponseEntity<List<RepaymentSchedule>> getRepaymentStatus(@PathVariable Integer accountId) {
        return ResponseEntity.ok(disbursementService.getRepaymentSchedule(accountId));
    }

 // 2. Simulate paying an installment (Securely tied to the account)
    @PutMapping("/account/{accountId}/pay/{installmentId}")
    public ResponseEntity<String> recordPayment(
            @PathVariable Integer accountId, 
            @PathVariable Integer installmentId) {
        
        disbursementService.updateInstallmentStatusSecurely(accountId, installmentId, "PAID");
        return ResponseEntity.ok("Payment successful for Account ID " + accountId + "!");
    }
}
package com.cblos.controller;

import com.cblos.model.LoanApplication;
import com.cblos.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanApplicationController {

    @Autowired
    private LoanApplicationService loanService;

    // 1. Submit a new application tied to a specific customer
    @PostMapping("/submit/{customerId}")
    public ResponseEntity<LoanApplication> submitApplication(
            @PathVariable Integer customerId, 
            @RequestBody LoanApplication application) {
        LoanApplication savedApplication = loanService.submitApplication(application, customerId);
        return ResponseEntity.ok(savedApplication);
    }

    // 2. Track status
    @GetMapping("/status/{id}")
    public ResponseEntity<String> trackApplicationStatus(@PathVariable Integer id) {
        return ResponseEntity.ok(loanService.getStatusById(id));
    }

    // 3. View full details
    @GetMapping("/{id}")
    public ResponseEntity<LoanApplication> viewApplicationDetails(@PathVariable Integer id) {
        return ResponseEntity.ok(loanService.getApplicationById(id));
    }
    
    // 4. View all applications
    @GetMapping("/all")
    public ResponseEntity<List<LoanApplication>> getAllApplications() {
        return ResponseEntity.ok(loanService.getAllApplications());
    }
    
    
 // 🧑‍💼 LOAN OFFICER WORKFLOW: Evaluate application and pass it up
    // PUT http://localhost:8080/api/loans/officer-review/1?score=720&notes=Vetted&pass=true
    @PutMapping("/officer-review/{id}")
    public ResponseEntity<LoanApplication> officerReview(
            @PathVariable Integer id,
            @RequestParam("score") Integer score,
            @RequestParam("notes") String notes,
            @RequestParam("pass") boolean pass) {
        // Calling the evaluation brain we structured
        return ResponseEntity.ok(loanService.officerEvaluateApplication(id, score, notes, pass));
    }

    // 🏢 MANAGER WORKFLOW: Final financial sign-off approval
    // PUT http://localhost:8080/api/loans/manager-approval/1?approve=true
    @PutMapping("/manager-approval/{id}")
    public ResponseEntity<LoanApplication> managerApproval(
            @PathVariable Integer id,
            @RequestParam("approve") boolean approve) {
        return ResponseEntity.ok(loanService.managerFinalDecision(id, approve));
    }
}
package com.cblos.controller;

import com.cblos.model.CreditAssessment;
import com.cblos.model.LoanApplication;
import com.cblos.service.CreditAssessmentService;
import com.cblos.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit")
public class CreditAssessmentController {

    @Autowired
    private CreditAssessmentService assessmentService;

    @Autowired
    private LoanApplicationService loanService;

    /**
     * POST /api/credit/evaluate/{applicationId}
     * Triggers the credit check process for a specific loan.
     */
    @PostMapping("/evaluate/{applicationId}")
    public ResponseEntity<CreditAssessment> evaluateCredit(@PathVariable Integer applicationId) {
        // Fetch the existing loan application
        LoanApplication application = loanService.getApplicationById(applicationId);
        
        // Run the evaluation logic
        CreditAssessment assessment = assessmentService.evaluateCredit(application);
        
        return ResponseEntity.ok(assessment);
    }

    /**
     * GET /api/credit/risk-score/{applicationId}
     * Retrieves only the calculated risk score.
     */
    @GetMapping("/risk-score/{applicationId}")
    public ResponseEntity<Double> getRiskScore(@PathVariable Integer applicationId) {
        Double score = assessmentService.getRiskScore(applicationId);
        return ResponseEntity.ok(score);
    }
}
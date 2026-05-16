package com.cblos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cblos.model.CreditAssessment;
import com.cblos.model.LoanApplication;
import com.cblos.repository.CreditAssessmentRepository;
import java.time.LocalDate;

@Service
public class CreditAssessmentService {

    @Autowired
    private CreditAssessmentRepository assessmentRepository;

    public CreditAssessment evaluateCredit(LoanApplication application) {
        CreditAssessment assessment = new CreditAssessment();
        assessment.setLoanApplication(application);
        assessment.setAssessmentDate(LocalDate.now());

        // Logic 1: External Score Simulation
        int externalScore = 750; 
        assessment.setCreditScore(externalScore);

        // Logic 2: Internal Risk Score Calculation
        double amount = (application.getLoanAmount() != null) ? application.getLoanAmount().doubleValue() : 0.0;
        double risk = (amount > 50000) ? 8.5 : 3.2;
        assessment.setRiskScore(risk);

        return assessmentRepository.save(assessment);
    }

    public Double getRiskScore(Integer applicationId) {
        return assessmentRepository.findByLoanApplication_ApplicationId(applicationId)
                .map(CreditAssessment::getRiskScore)
                .orElse(0.0);
    }
}
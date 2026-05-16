package com.cblos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan_application")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applicationId;

    // Relationship: Many applications can belong to one Corporate Customer
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CorporateCustomer customer;

    // Relationship: Many applications can be assigned to one Loan Officer
    @ManyToOne
    @JoinColumn(name = "officer_id")
    private LoanOfficer loanOfficer; // Can be null initially until assigned

    private String loanType;
    private BigDecimal loanAmount;
    private String status;
    private LocalDate submissionDate;

    // Default constructor
    public LoanApplication() {}

    // Getters and Setters
    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }

    public CorporateCustomer getCustomer() { return customer; }
    public void setCustomer(CorporateCustomer customer) { this.customer = customer; }

    public LoanOfficer getLoanOfficer() { return loanOfficer; }
    public void setLoanOfficer(LoanOfficer loanOfficer) { this.loanOfficer = loanOfficer; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public BigDecimal getLoanAmount() { return loanAmount; }
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDate submissionDate) { this.submissionDate = submissionDate; }
}
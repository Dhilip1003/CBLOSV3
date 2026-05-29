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

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CorporateCustomer customer;

    @ManyToOne
    @JoinColumn(name = "officer_id")
    private LoanOfficer loanOfficer; 

    // ─── 🔗 NEW: Relationship linking this request to the strict product rule catalog ───
    @ManyToOne
    @JoinColumn(name = "loan_product_id")
    private LoanProduct loanProduct; 

    private String loanType; // Can match loanProduct.getProductName() dynamically
    private BigDecimal loanAmount;
    private String status;
    private LocalDate submissionDate;

    // ─── 📅 NEW: Field tracking the customer's selected customized payback timeframe ───
    @Column(name = "requested_tenure_months")
    private Integer requestedTenureMonths; 

    public LoanApplication() {}

    // --- YOUR EXISTING GETTERS & SETTERS ---
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

    // --- 🧬 NEW GETTERS AND SETTERS FOR THE UPDATED FIELDS ---
    public LoanProduct getLoanProduct() { return loanProduct; }
    public void setLoanProduct(LoanProduct loanProduct) { this.loanProduct = loanProduct; }

    public Integer getRequestedTenureMonths() { return requestedTenureMonths; }
    public void setRequestedTenureMonths(Integer requestedTenureMonths) { this.requestedTenureMonths = requestedTenureMonths; }
}
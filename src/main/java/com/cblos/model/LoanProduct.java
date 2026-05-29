package com.cblos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "loan_products")
public class LoanProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // The unique ID for the product (e.g., 1, 2, 3)

    @Column(name = "product_name", nullable = false, unique = true)
    private String productName; // e.g., "COMMERCIAL_TERM_LOAN"

    @Column(length = 1000)
    private String description; // Explains what the loan is used for

    @Column(name = "min_loan_amount", nullable = false)
    private Double minLoanAmount; // The lowest amount a business can ask for

    @Column(name = "max_loan_amount", nullable = false)
    private Double maxLoanAmount; // The maximum cap the bank is willing to risk

    @Column(name = "default_interest_rate", nullable = false)
    private Double defaultInterestRate; // The standard annual interest rate percentage

    @Column(name = "min_tenure_months", nullable = false)
    private Integer minTenureMonths; // e.g., 6 months minimum

    @Column(name = "max_tenure_months", nullable = false)
    private Integer maxTenureMonths; // e.g., 60 months maximum
    
    
    // Empty Constructor required by JPA
    public LoanProduct() {}

    // --- GETTERS AND SETTERS (Allows other classes to read and write data safely) ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getMinLoanAmount() { return minLoanAmount; }
    public void setMinLoanAmount(Double minLoanAmount) { this.minLoanAmount = minLoanAmount; }

    public Double getMaxLoanAmount() { return maxLoanAmount; }
    public void setMaxLoanAmount(Double maxLoanAmount) { this.maxLoanAmount = maxLoanAmount; }

    public Double getDefaultInterestRate() { return defaultInterestRate; }
    public void setDefaultInterestRate(Double defaultInterestRate) { this.defaultInterestRate = defaultInterestRate; }
    
    public Integer getMinTenureMonths() { return minTenureMonths; }
    public void setMinTenureMonths(Integer minTenureMonths) { this.minTenureMonths = minTenureMonths; }

    public Integer getMaxTenureMonths() { return maxTenureMonths; }
    public void setMaxTenureMonths(Integer maxTenureMonths) { this.maxTenureMonths = maxTenureMonths; }
}
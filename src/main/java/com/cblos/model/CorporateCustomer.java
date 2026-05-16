package com.cblos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "corporate_customer")
public class CorporateCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tax_id", unique = true, nullable = false)
    private String taxId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "industry_type")
    private String industryType;

    // Default constructor
    public CorporateCustomer() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIndustryType() { return industryType; }
    public void setIndustryType(String industryType) { this.industryType = industryType; }
}
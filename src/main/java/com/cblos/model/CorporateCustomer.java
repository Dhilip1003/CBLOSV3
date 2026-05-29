package com.cblos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "corporate_customer")
public class CorporateCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "tax_id", unique = true, nullable = false)
    private String taxId; 

    @Column(name = "registration_number", unique = true)
    private String registrationNumber; 

    @Column(name = "company_email", unique = true, nullable = false)
    private String companyEmail;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "business_address", length = 500)
    private String businessAddress;

    @Column(name = "industry_type")
    private String industryType;

    @Column(name = "annual_revenue", precision = 18, scale = 2)
    private BigDecimal annualRevenue; 

    @Column(name = "years_in_business")
    private Integer yearsInBusiness; 

    // ─── NEW REAL-WORLD LEGITIMACY FIELD ───
    @Column(name = "legal_document_status", nullable = false)
    private String legalDocumentStatus = "UNVERIFIED"; // UNVERIFIED, VERIFIED_LEGITIMATE, REJECTED

    @Column(nullable = false)
    private String status = "PENDING_VERIFICATION"; 
    
    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    // Default Constructor
    public CorporateCustomer() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getCompanyEmail() { return companyEmail; }
    public void setCompanyEmail(String companyEmail) { this.companyEmail = companyEmail; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }

    public String getIndustryType() { return industryType; }
    public void setIndustryType(String industryType) { this.industryType = industryType; }

    public BigDecimal getAnnualRevenue() { return annualRevenue; }
    public void setAnnualRevenue(BigDecimal annualRevenue) { this.annualRevenue = annualRevenue; }

    public Integer getYearsInBusiness() { return yearsInBusiness; }
    public void setYearsInBusiness(Integer yearsInBusiness) { this.yearsInBusiness = yearsInBusiness; }

    // Getter and Setter for Legitimacy Check
    public String getLegalDocumentStatus() { return legalDocumentStatus; }
    public void setLegalDocumentStatus(String legalDocumentStatus) { this.legalDocumentStatus = legalDocumentStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
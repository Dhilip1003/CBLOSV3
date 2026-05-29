package com.cblos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer documentId;

    // ─── FIX: Changed nullable to true so registration documents don't require a loan yet ───
    @ManyToOne
    @JoinColumn(name = "applicationId", nullable = true)
    private LoanApplication loanApplication;

    // ─── NEW: Links directly to the Corporate Customer for legal onboarding ───
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private CorporateCustomer corporateCustomer;

    private String documentType; 
    private String fileName;
    private String fileType;

    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData; 

    private LocalDateTime uploadDate;
    private Boolean isValid;

    public Document() {
        this.uploadDate = LocalDateTime.now();
        this.isValid = false;
    }

    // --- GETTERS AND SETTERS ---
    public Integer getDocumentId() { return documentId; }
    public void setDocumentId(Integer documentId) { this.documentId = documentId; }

    public LoanApplication getLoanApplication() { return loanApplication; }
    public void setLoanApplication(LoanApplication loanApplication) { this.loanApplication = loanApplication; }

    // New Getter/Setter for Corporate Customer link
    public CorporateCustomer getCorporateCustomer() { return corporateCustomer; }
    public void setCorporateCustomer(CorporateCustomer corporateCustomer) { this.corporateCustomer = corporateCustomer; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    public Boolean getIsValid() { return isValid; }
    public void setIsValid(Boolean isValid) { this.isValid = isValid; }
}
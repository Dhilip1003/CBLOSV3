package com.cblos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer documentId;

    @ManyToOne
    @JoinColumn(name = "applicationId", nullable = false)
    private LoanApplication loanApplication;

    private String documentType; 
    
    // 1. CHANGE: Added fileName and fileType for easier downloading later
    private String fileName;
    private String fileType;

    // 2. CHANGE: Replaced filePath with binary fileData
    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData; 

    private LocalDateTime uploadDate;

    private Boolean isValid;

    public Document() {
        this.uploadDate = LocalDateTime.now();
        this.isValid = false;
    }

    // --- UPDATED GETTERS AND SETTERS ---

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    // (Keep your existing getters/setters for ID, LoanApp, DocType, etc.)
    public Integer getDocumentId() { return documentId; }
    public void setDocumentId(Integer documentId) { this.documentId = documentId; }
    public LoanApplication getLoanApplication() { return loanApplication; }
    public void setLoanApplication(LoanApplication loanApplication) { this.loanApplication = loanApplication; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
    public Boolean getIsValid() { return isValid; }
    public void setIsValid(Boolean isValid) { this.isValid = isValid; }
}
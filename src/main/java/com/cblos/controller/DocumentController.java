package com.cblos.controller;

import com.cblos.dto.DocumentSummary;
import com.cblos.model.CorporateCustomer;
import com.cblos.model.Document;
import com.cblos.model.LoanApplication;
import com.cblos.repository.CorporateCustomerRepository;
import com.cblos.repository.DocumentRepository;
import com.cblos.repository.LoanApplicationRepository;
import com.cblos.security.AccessControlService;
import com.cblos.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private LoanApplicationRepository loanRepository;

    @Autowired
    private CorporateCustomerRepository customerRepository; // ─── NEW: Added to fetch onboarding context ───

    @Autowired
    private DocumentService documentService;

    @Autowired
    private AccessControlService accessControl;

    // 1. Existing Method: Upload a document tied to a Loan Application
    @PostMapping("/upload/{applicationId}")
    public ResponseEntity<String> uploadDocument(
            @PathVariable Integer applicationId,
            @RequestParam("documentType") String documentType, // Added parameter to keep service layer explicit
            @RequestParam("file") MultipartFile file) throws IOException {

        accessControl.ensureCustomerOwnsApplication(applicationId);

        LoanApplication app = loanRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        documentService.uploadDocument(
                app,
                documentType,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes());

        return ResponseEntity.ok("Document uploaded successfully for Application ID: " + applicationId);
    }

    // 2. ─── NEW ENDPOINT: Upload foundational legal documents during initial Customer Registration ───
    @PostMapping("/upload/registration/{customerId}")
    public ResponseEntity<String> uploadRegistrationDocument(
            @PathVariable Integer customerId,
            @RequestParam("documentType") String documentType, // e.g., CERTIFICATE_OF_INCORPORATION
            @RequestParam("file") MultipartFile file) throws IOException {

        // Enforce that the logged-in user context matches the customer record being modified
        accessControl.ensureCustomerOwnsCustomerRecord(customerId);

        CorporateCustomer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Corporate Customer profile not found"));

        documentService.uploadRegistrationDocument(
                customer,
                documentType,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Legal registration document uploaded successfully: " + file.getOriginalFilename());
    }

    // 3. Download/View a document
    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> getDocument(@PathVariable Integer documentId) {
        accessControl.ensureCustomerOwnsDocument(documentId);
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(doc.getFileData());
    }
    
    // 4. List document metadata only (no file bytes) for an application
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<DocumentSummary>> listDocs(@PathVariable Integer applicationId) {
        accessControl.ensureCustomerOwnsApplication(applicationId);
        return ResponseEntity.ok(documentService.listSummariesForApplication(applicationId));
    }

    // 5. Validate a specific document
    @PutMapping("/validate/{documentId}")
    public ResponseEntity<String> validateDocument(@PathVariable Integer documentId) {
        Document validatedDoc = documentService.validateDocument(documentId);
        if (validatedDoc.getIsValid()) {
            return ResponseEntity.ok("Document validated successfully. Document ID: " + documentId);
        } else {
            return ResponseEntity.badRequest().body("Document validation failed. Invalid or empty file.");
        }
    }

    // 6. Get validation status of a document
    @GetMapping("/validate/{documentId}")
    public ResponseEntity<String> getValidationStatus(@PathVariable Integer documentId) {
        accessControl.ensureCustomerOwnsDocument(documentId);
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        
        String status = doc.getIsValid() ? "Valid" : "Not Validated";
        return ResponseEntity.ok("Document ID: " + documentId + " | Status: " + status + " | File: " + doc.getFileName());
    }

    // 7. Validate all documents for an application
    @PutMapping("/validate-all/{applicationId}")
    public ResponseEntity<String> validateAllDocuments(@PathVariable Integer applicationId) {
        List<Document> documents = documentRepository.findByLoanApplication_ApplicationId(applicationId);
        
        if (documents.isEmpty()) {
            return ResponseEntity.ok("No documents found for Application ID: " + applicationId);
        }
        
        int validatedCount = 0;
        for (Document doc : documents) {
            Document validatedDoc = documentService.validateDocument(doc.getDocumentId());
            if (validatedDoc.getIsValid()) {
                validatedCount++;
            }
        }
        
        return ResponseEntity.ok("Validated " + validatedCount + " out of " + documents.size() + " documents for Application ID: " + applicationId);
    }

    // 8. Get validation report for an application
    @GetMapping("/validation-report/{applicationId}")
    public ResponseEntity<String> getValidationReport(@PathVariable Integer applicationId) {
        accessControl.ensureCustomerOwnsApplication(applicationId);
        List<Document> documents = documentRepository.findByLoanApplication_ApplicationId(applicationId);
        
        if (documents.isEmpty()) {
            return ResponseEntity.ok("No documents found for Application ID: " + applicationId);
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=== DOCUMENT VALIDATION REPORT ===\n");
        report.append("Application ID: ").append(applicationId).append("\n");
        report.append("Total Documents: ").append(documents.size()).append("\n\n");
        
        int validCount = 0;
        int invalidCount = 0;
        
        for (Document doc : documents) {
            report.append("Document ID: ").append(doc.getDocumentId()).append("\n");
            report.append("  File Name: ").append(doc.getFileName()).append("\n");
            report.append("  File Type: ").append(doc.getFileType()).append("\n");
            report.append("  Upload Date: ").append(doc.getUploadDate()).append("\n");
            report.append("  Status: ").append(doc.getIsValid() ? "VALID" : "INVALID").append("\n");
            report.append("  File Size: ").append(doc.getFileData().length).append(" bytes\n\n");
            
            if (doc.getIsValid()) {
                validCount++;
            } else {
                invalidCount++;
            }
        }
        
        report.append("===================================\n");
        report.append("Valid Documents: ").append(validCount).append("\n");
        report.append("Invalid/Unvalidated Documents: ").append(invalidCount).append("\n");
        
        return ResponseEntity.ok(report.toString());
    }
}
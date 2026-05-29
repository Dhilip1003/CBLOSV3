package com.cblos.service;

import com.cblos.dto.DocumentSummary;
import com.cblos.model.CorporateCustomer;
import com.cblos.model.Document;
import com.cblos.model.LoanApplication;
import com.cblos.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    // 1. Existing Method: For uploading documents tied to a specific Loan Application
    public Document uploadDocument(LoanApplication application, String type, String fileName, String fileType, byte[] data) {
        Document doc = new Document();
        doc.setLoanApplication(application);
        if (application != null) {
            doc.setCorporateCustomer(application.getCustomer());
        }
        doc.setDocumentType(type);
        
        // Setting the BLOB fields
        doc.setFileName(fileName);
        doc.setFileType(fileType);
        doc.setFileData(data); 
        
        doc.setUploadDate(LocalDateTime.now());
        doc.setIsValid(false); // Keeps it false until an officer verifies it
        
        return documentRepository.save(doc);
    }

    // 2. ─── NEW METHOD: For uploading legal KYB documents during initial Corporate Registration ───
    public Document uploadRegistrationDocument(CorporateCustomer customer, String type, String fileName, String fileType, byte[] data) {
        Document doc = new Document();
        doc.setCorporateCustomer(customer);
        doc.setLoanApplication(null); // Explicitly null because no loan application exists yet
        doc.setDocumentType(type); // e.g., "CERTIFICATE_OF_INCORPORATION"
        
        doc.setFileName(fileName);
        doc.setFileType(fileType);
        doc.setFileData(data);
        
        doc.setUploadDate(LocalDateTime.now());
        doc.setIsValid(false); // Default to false until bank officer validation
        
        return documentRepository.save(doc);
    }

    // Officer confirms the document is acceptable (upload alone does not validate)
    public Document validateDocument(Integer documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (doc.getFileData() == null || doc.getFileData().length == 0) {
            doc.setIsValid(false);
            return documentRepository.save(doc);
        }

        doc.setIsValid(true);
        return documentRepository.save(doc);
    }

    public List<Document> getDocumentsByLoan(Integer applicationId) {
        return documentRepository.findByLoanApplication_ApplicationId(applicationId);
    }

    // 3. ─── NEW METHOD: Fetch documents tied to the customer identity ───
    public List<Document> getDocumentsByCustomer(Integer customerId) {
        return documentRepository.findByCorporateCustomer_Id(customerId);
    }

    public List<DocumentSummary> listSummariesForApplication(Integer applicationId) {
        return getDocumentsByLoan(applicationId).stream()
                .map(DocumentSummary::from)
                .toList();
    }
    
    // Method to fetch a specific document for downloading
    public Document getDocumentById(Integer documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }
}
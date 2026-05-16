package com.cblos.service;

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

    // 1. UPDATED: Accepts bytes, fileName, and fileType instead of a String path
    public Document uploadDocument(LoanApplication application, String type, String fileName, String fileType, byte[] data) {
        Document doc = new Document();
        doc.setLoanApplication(application);
        doc.setDocumentType(type);
        
        // Setting the new BLOB fields
        doc.setFileName(fileName);
        doc.setFileType(fileType);
        doc.setFileData(data); 
        
        doc.setUploadDate(LocalDateTime.now());
        doc.setIsValid(false); // Keeps it false until an officer verifies it
        
        return documentRepository.save(doc);
    }

    // 2. UPDATED: Validation now checks if fileData is present
    public Document validateDocument(Integer documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        
        // Logic: If the byte array is not null and has content, it's a valid upload
        if (doc.getFileData() != null && doc.getFileData().length > 0) {
            doc.setIsValid(true);
        }
        
        return documentRepository.save(doc);
    }

    public List<Document> getDocumentsByLoan(Integer applicationId) {
        return documentRepository.findByLoanApplication_ApplicationId(applicationId);
    }
    
    // 3. NEW: Method to fetch a specific document for downloading
    public Document getDocumentById(Integer documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }
}
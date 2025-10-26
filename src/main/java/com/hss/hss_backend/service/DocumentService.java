package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.DocumentCreateRequest;
import com.hss.hss_backend.dto.request.DocumentUpdateRequest;
import com.hss.hss_backend.dto.response.DocumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DocumentService {
    
    DocumentResponse createDocument(DocumentCreateRequest request);
    
    DocumentResponse getDocumentById(Long id);
    
    Page<DocumentResponse> getAllDocuments(Pageable pageable);
    
    List<DocumentResponse> getDocumentsByOwner(Long ownerId);
    
    List<DocumentResponse> getDocumentsByAnimal(Long animalId);
    
    List<DocumentResponse> searchDocumentsByTitle(String title);
    
    DocumentResponse updateDocument(Long id, DocumentUpdateRequest request);
    
    void deleteDocument(Long id);
}


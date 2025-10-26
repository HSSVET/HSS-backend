package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.DocumentCreateRequest;
import com.hss.hss_backend.dto.request.DocumentUpdateRequest;
import com.hss.hss_backend.dto.response.DocumentResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Document;
import com.hss.hss_backend.entity.Owner;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.DocumentMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.DocumentRepository;
import com.hss.hss_backend.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final OwnerRepository ownerRepository;
    private final AnimalRepository animalRepository;

    @Override
    public DocumentResponse createDocument(DocumentCreateRequest request) {
        log.info("Creating document: {}", request.getTitle());
        
        Owner owner = ownerRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + request.getOwnerId()));
        
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with ID: " + request.getAnimalId()));
        
        Document document = DocumentMapper.toEntity(request, owner, animal);
        Document savedDocument = documentRepository.save(document);
        
        log.info("Successfully created document with ID: {}", savedDocument.getDocumentId());
        return DocumentMapper.toResponse(savedDocument);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(Long id) {
        log.info("Fetching document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));
        
        // Fetch owner and animal to avoid lazy loading issues
        document.getOwner().getFirstName();
        document.getAnimal().getName();
        
        return DocumentMapper.toResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponse> getAllDocuments(Pageable pageable) {
        log.info("Fetching all documents with pagination");
        Page<Document> documents = documentRepository.findAll(pageable);
        return documents.map(DocumentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByOwner(Long ownerId) {
        log.info("Fetching documents for owner ID: {}", ownerId);
        List<Document> documents = documentRepository.findByOwner_OwnerId(ownerId);
        return DocumentMapper.toResponseList(documents);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByAnimal(Long animalId) {
        log.info("Fetching documents for animal ID: {}", animalId);
        List<Document> documents = documentRepository.findByAnimal_AnimalId(animalId);
        return DocumentMapper.toResponseList(documents);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> searchDocumentsByTitle(String title) {
        log.info("Searching documents by title: {}", title);
        List<Document> documents = documentRepository.findByTitleContaining(title);
        return DocumentMapper.toResponseList(documents);
    }

    @Override
    public DocumentResponse updateDocument(Long id, DocumentUpdateRequest request) {
        log.info("Updating document with ID: {}", id);
        
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));
        
        DocumentMapper.updateEntity(document, request);
        Document savedDocument = documentRepository.save(document);
        
        log.info("Successfully updated document with ID: {}", savedDocument.getDocumentId());
        return DocumentMapper.toResponse(savedDocument);
    }

    @Override
    public void deleteDocument(Long id) {
        log.info("Deleting document with ID: {}", id);
        
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));
        
        documentRepository.delete(document);
        log.info("Successfully deleted document with ID: {}", id);
    }
}


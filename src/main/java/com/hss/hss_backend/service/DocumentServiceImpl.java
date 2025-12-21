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
import com.hss.hss_backend.security.ClinicContext;
import org.springframework.security.access.AccessDeniedException;
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
    private final DocumentMapper documentMapper;

    @Override
    public DocumentResponse createDocument(DocumentCreateRequest request) {
        log.info("Creating document: {}", request.getTitle());

        Owner owner = ownerRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + request.getOwnerId()));

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with ID: " + request.getAnimalId()));

        Document document = documentMapper.toEntity(request, owner, animal);

        // Ensure owner belongs to current clinic if clinic context is active
        validateOwnerClinicAccess(owner);

        Document savedDocument = documentRepository.save(document);

        log.info("Successfully created document with ID: {}", savedDocument.getDocumentId());
        return documentMapper.toResponse(savedDocument);
    }

    private void validateOwnerClinicAccess(Owner owner) {
        Long currentClinicId = ClinicContext.getClinicId();
        if (currentClinicId != null && owner.getClinic() != null) {
            Long ownerClinicId = owner.getClinic().getClinicId();
            if (!currentClinicId.equals(ownerClinicId)) {
                log.error("Access denied. Clinic ID {} cannot create/access document for owner {} in clinic {}",
                        currentClinicId, owner.getOwnerId(), ownerClinicId);
                throw new AccessDeniedException("You do not have permission to access this resource.");
            }
        }
    }

    private void validateDocumentAccess(Document document) {
        if (document.getOwner() != null) {
            validateOwnerClinicAccess(document.getOwner());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(Long id) {
        log.info("Fetching document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));

        // Fetch owner and animal to avoid lazy loading issues
        if (document.getOwner() != null) {
            document.getOwner().getFirstName();
        }
        if (document.getAnimal() != null) {
            document.getAnimal().getName();
        }

        validateDocumentAccess(document);

        return documentMapper.toResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponse> getAllDocuments(Pageable pageable) {
        log.info("Fetching all documents with pagination");
        Long clinicId = ClinicContext.getClinicId();

        Page<Document> documents;
        if (clinicId != null) {
            documents = documentRepository.findByClinicId(clinicId, pageable);
        } else {
            documents = documentRepository.findAll(pageable);
        }

        return documents.map(documentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByOwner(Long ownerId) {
        log.info("Fetching documents for owner ID: {}", ownerId);
        List<Document> documents = documentRepository.findActiveByOwnerId(ownerId);
        return documentMapper.toResponseList(documents);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByAnimal(Long animalId) {
        log.info("Fetching documents for animal ID: {}", animalId);
        List<Document> documents = documentRepository.findActiveByAnimalId(animalId);
        return documentMapper.toResponseList(documents);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> searchDocumentsByTitle(String title) {
        log.info("Searching documents by title: {}", title);
        List<Document> documents = documentRepository.searchByTitle(title);
        return documentMapper.toResponseList(documents);
    }

    @Override
    public DocumentResponse updateDocument(Long id, DocumentUpdateRequest request) {
        log.info("Updating document with ID: {}", id);

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));

        documentMapper.updateEntity(document, request);

        validateDocumentAccess(document);

        Document savedDocument = documentRepository.save(document);

        log.info("Successfully updated document with ID: {}", savedDocument.getDocumentId());
        return documentMapper.toResponse(savedDocument);
    }

    @Override
    public void deleteDocument(Long id) {
        log.info("Deleting document with ID: {}", id);

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));

        validateDocumentAccess(document);

        documentRepository.delete(document);

        log.info("Successfully deleted document with ID: {}", id);
    }
}

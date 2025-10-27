package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.DocumentCreateDTO;
import com.hss.hss_backend.dto.DocumentResponseDTO;
import com.hss.hss_backend.dto.DocumentUpdateDTO;
import com.hss.hss_backend.dto.FileUploadResponseDTO;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Document;
import com.hss.hss_backend.entity.Owner;
import com.hss.hss_backend.exception.DocumentNotFoundException;
import com.hss.hss_backend.exception.FileStorageException;
import com.hss.hss_backend.mapper.DocumentMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.DocumentRepository;
import com.hss.hss_backend.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.cloud.gcp.storage.enabled", havingValue = "true", matchIfMissing = false)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OwnerRepository ownerRepository;
    private final AnimalRepository animalRepository;
    private final StorageService storageService;
    private final DocumentMapper documentMapper;

    @Transactional
    public DocumentResponseDTO createDocument(DocumentCreateDTO createDTO) {
        log.info("Creating document: {}", createDTO.getTitle());

        // Owner ve Animal'ın varlığını kontrol et
        Owner owner = ownerRepository.findById(createDTO.getOwnerId())
                .orElseThrow(() -> new DocumentNotFoundException("Owner not found with id: " + createDTO.getOwnerId()));

        Animal animal = animalRepository.findById(createDTO.getAnimalId())
                .orElseThrow(
                        () -> new DocumentNotFoundException("Animal not found with id: " + createDTO.getAnimalId()));

        Document document = documentMapper.toEntity(createDTO);
        document.setOwner(owner);
        document.setAnimal(animal);

        Document savedDocument = documentRepository.save(document);

        log.info("Document created successfully with id: {}", savedDocument.getDocumentId());
        return documentMapper.toResponseDTO(savedDocument);
    }

    @Transactional
    public FileUploadResponseDTO uploadFileWithDocument(MultipartFile file, DocumentCreateDTO createDTO,
            String folder) {
        log.info("Uploading file with document: {} for owner: {}, animal: {}",
                file.getOriginalFilename(), createDTO.getOwnerId(), createDTO.getAnimalId());

        try {
            // Dosyayı yükle ve metadata al
            String fileUrl = storageService.uploadFile(file, folder);
            // saat

            // Document entity oluştur
            Document document = documentMapper.toEntity(createDTO);
            document.setFileUrl(fileUrl);
            document.setFileName(file.getOriginalFilename());
            document.setFileSize(file.getSize());
            document.setMimeType(file.getContentType());

            // Owner ve Animal'ı set et
            Owner owner = ownerRepository.findById(createDTO.getOwnerId())
                    .orElseThrow(
                            () -> new DocumentNotFoundException("Owner not found with id: " + createDTO.getOwnerId()));
            Animal animal = animalRepository.findById(createDTO.getAnimalId())
                    .orElseThrow(() -> new DocumentNotFoundException(
                            "Animal not found with id: " + createDTO.getAnimalId()));

            document.setOwner(owner);
            document.setAnimal(animal);

            Document savedDocument = documentRepository.save(document);

            // Response'a document ID ekle
            FileUploadResponseDTO uploadResponse = new FileUploadResponseDTO(fileUrl, file.getOriginalFilename(),
                    file.getSize(), file.getContentType(), null, null, null);
            uploadResponse.setDocumentId(savedDocument.getDocumentId());

            log.info("File uploaded and document created successfully with id: {}", savedDocument.getDocumentId());
            return uploadResponse;

        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            throw new FileStorageException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    public DocumentResponseDTO getDocumentById(Long documentId) {
        log.info("Getting document by id: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));

        return documentMapper.toResponseDTO(document);
    }

    public Page<DocumentResponseDTO> getAllDocuments(Pageable pageable) {
        log.info("Getting all documents with pagination");

        Page<Document> documents = documentRepository.findAll(pageable);
        return documents.map(documentMapper::toResponseDTO);
    }

    public List<DocumentResponseDTO> getDocumentsByOwnerId(Long ownerId) {
        log.info("Getting documents by owner id: {}", ownerId);

        List<Document> documents = documentRepository.findActiveByOwnerId(ownerId);
        return documents.stream()
                .map(documentMapper::toResponseDTO)
                .toList();
    }

    public List<DocumentResponseDTO> getDocumentsByAnimalId(Long animalId) {
        log.info("Getting documents by animal id: {}", animalId);

        List<Document> documents = documentRepository.findActiveByAnimalId(animalId);
        return documents.stream()
                .map(documentMapper::toResponseDTO)
                .toList();
    }

    public List<DocumentResponseDTO> getDocumentsByType(Document.DocumentType documentType) {
        log.info("Getting documents by type: {}", documentType);

        List<Document> documents = documentRepository.findByDocumentTypeWithDetails(documentType);
        return documents.stream()
                .map(documentMapper::toResponseDTO)
                .toList();
    }

    public List<DocumentResponseDTO> searchDocumentsByTitle(String title) {
        log.info("Searching documents by title: {}", title);

        List<Document> documents = documentRepository.searchByTitle(title);
        return documents.stream()
                .map(documentMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public DocumentResponseDTO updateDocument(Long documentId, DocumentUpdateDTO updateDTO) {
        log.info("Updating document with id: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));

        documentMapper.updateEntityFromDTO(updateDTO, document);
        Document updatedDocument = documentRepository.save(document);

        log.info("Document updated successfully with id: {}", documentId);
        return documentMapper.toResponseDTO(updatedDocument);
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        log.info("Deleting document with id: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));

        // Eğer dosya varsa Cloud Storage'dan sil
        if (document.getFileUrl() != null) {
            try {
                storageService.deleteFile(document.getFileUrl());
                log.info("File deleted from storage: {}", document.getFileUrl());
            } catch (Exception e) {
                log.warn("Failed to delete file from storage: {}", e.getMessage());
                // Dosya silinemedi ama devam et
            }
        }

        // Soft delete - arşivle
        document.setIsArchived(true);
        documentRepository.save(document);

        log.info("Document archived successfully with id: {}", documentId);
    }

    public String generateSignedUrlForDocument(Long documentId, long expirationTime) {
        log.info("Generating signed URL for document id: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));

        if (document.getFileUrl() == null) {
            throw new DocumentNotFoundException("Document has no associated file");
        }

        return storageService.generateSignedUrl(document.getFileUrl(), expirationTime);
    }

    public FileMetadata getFileMetadataForDocument(Long documentId) {
        log.info("Getting file metadata for document id: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));

        if (document.getFileUrl() == null) {
            throw new DocumentNotFoundException("Document has no associated file");
        }

        return FileMetadata.builder()
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .fileSize(document.getFileSize())
                .contentType(document.getMimeType())
                .build();
    }

    public List<DocumentResponseDTO> getDocumentsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Getting documents by date range: {} to {}", startDate, endDate);

        List<Document> documents = documentRepository.findByDateRange(startDate, endDate);
        return documents.stream()
                .map(documentMapper::toResponseDTO)
                .toList();
    }

    public List<DocumentResponseDTO> getDocumentsByOwnerAndDateRange(Long ownerId, LocalDate startDate,
            LocalDate endDate) {
        log.info("Getting documents by owner {} and date range: {} to {}", ownerId, startDate, endDate);

        List<Document> documents = documentRepository.findByOwnerIdAndDateRange(ownerId, startDate, endDate);
        return documents.stream()
                .map(documentMapper::toResponseDTO)
                .toList();
    }
}

package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.DocumentCreateDTO;
import com.hss.hss_backend.dto.DocumentResponseDTO;
import com.hss.hss_backend.dto.DocumentUpdateDTO;
import com.hss.hss_backend.entity.Document;
import com.hss.hss_backend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<Page<DocumentResponseDTO>> getAllDocuments(Pageable pageable) {
        log.info("Getting all documents with pagination");

        Page<DocumentResponseDTO> documents = documentService.getAllDocuments(pageable);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<DocumentResponseDTO> getDocumentById(@PathVariable Long id) {
        log.info("Getting document by id: {}", id);

        DocumentResponseDTO document = documentService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<DocumentResponseDTO> createDocument(@Valid @RequestBody DocumentCreateDTO createDTO) {
        log.info("Creating document: {}", createDTO.getTitle());

        DocumentResponseDTO document = documentService.createDocument(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<DocumentResponseDTO> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentUpdateDTO updateDTO) {
        log.info("Updating document with id: {}", id);

        DocumentResponseDTO document = documentService.updateDocument(id, updateDTO);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        log.info("Deleting document with id: {}", id);

        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<List<DocumentResponseDTO>> getDocumentsByOwnerId(@PathVariable Long ownerId) {
        log.info("Getting documents by owner id: {}", ownerId);

        List<DocumentResponseDTO> documents = documentService.getDocumentsByOwnerId(ownerId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<List<DocumentResponseDTO>> getDocumentsByAnimalId(@PathVariable Long animalId) {
        log.info("Getting documents by animal id: {}", animalId);

        List<DocumentResponseDTO> documents = documentService.getDocumentsByAnimalId(animalId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/type/{documentType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<List<DocumentResponseDTO>> getDocumentsByType(
            @PathVariable Document.DocumentType documentType) {
        log.info("Getting documents by type: {}", documentType);

        List<DocumentResponseDTO> documents = documentService.getDocumentsByType(documentType);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<List<DocumentResponseDTO>> searchDocumentsByTitle(@RequestParam String title) {
        log.info("Searching documents by title: {}", title);

        List<DocumentResponseDTO> documents = documentService.searchDocumentsByTitle(title);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<List<DocumentResponseDTO>> getDocumentsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        log.info("Getting documents by date range: {} to {}", startDate, endDate);

        List<DocumentResponseDTO> documents = documentService.getDocumentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/owner/{ownerId}/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<List<DocumentResponseDTO>> getDocumentsByOwnerAndDateRange(
            @PathVariable Long ownerId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        log.info("Getting documents by owner {} and date range: {} to {}", ownerId, startDate, endDate);

        List<DocumentResponseDTO> documents = documentService.getDocumentsByOwnerAndDateRange(ownerId, startDate,
                endDate);
        return ResponseEntity.ok(documents);
    }
}

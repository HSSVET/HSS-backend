package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.DocumentCreateRequest;
import com.hss.hss_backend.dto.request.DocumentUpdateRequest;
import com.hss.hss_backend.dto.response.DocumentResponse;
import com.hss.hss_backend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<Page<DocumentResponse>> getAllDocuments(Pageable pageable) {
        log.info("Getting all documents with pagination");

        Page<DocumentResponse> documents = documentService.getAllDocuments(pageable);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long id) {
        log.info("Getting document by id: {}", id);

        DocumentResponse document = documentService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<DocumentResponse> createDocument(@Valid @RequestBody DocumentCreateRequest request) {
        log.info("Creating document: {}", request.getTitle());

        DocumentResponse document = documentService.createDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<DocumentResponse> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentUpdateRequest request) {
        log.info("Updating document with id: {}", id);

        DocumentResponse document = documentService.updateDocument(id, request);
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
    public ResponseEntity<List<DocumentResponse>> getDocumentsByOwner(@PathVariable Long ownerId) {
        log.info("Getting documents by owner id: {}", ownerId);

        List<DocumentResponse> documents = documentService.getDocumentsByOwner(ownerId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByAnimal(@PathVariable Long animalId) {
        log.info("Getting documents by animal id: {}", animalId);

        List<DocumentResponse> documents = documentService.getDocumentsByAnimal(animalId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<List<DocumentResponse>> searchDocumentsByTitle(@RequestParam String title) {
        log.info("Searching documents by title: {}", title);

        List<DocumentResponse> documents = documentService.searchDocumentsByTitle(title);
        return ResponseEntity.ok(documents);
    }
}

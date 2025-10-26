package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.DocumentCreateRequest;
import com.hss.hss_backend.dto.request.DocumentUpdateRequest;
import com.hss.hss_backend.dto.response.DocumentResponse;
import com.hss.hss_backend.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<DocumentResponse> createDocument(@Valid @RequestBody DocumentCreateRequest request) {
        log.info("Creating document: {}", request.getTitle());
        DocumentResponse response = documentService.createDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long id) {
        log.info("Fetching document with ID: {}", id);
        DocumentResponse response = documentService.getDocumentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<DocumentResponse>> getAllDocuments(Pageable pageable) {
        log.info("Fetching all documents with pagination");
        Page<DocumentResponse> response = documentService.getAllDocuments(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByOwner(@PathVariable Long ownerId) {
        log.info("Fetching documents for owner ID: {}", ownerId);
        List<DocumentResponse> response = documentService.getDocumentsByOwner(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByAnimal(@PathVariable Long animalId) {
        log.info("Fetching documents for animal ID: {}", animalId);
        List<DocumentResponse> response = documentService.getDocumentsByAnimal(animalId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<DocumentResponse>> searchDocumentsByTitle(@RequestParam String title) {
        log.info("Searching documents by title: {}", title);
        List<DocumentResponse> response = documentService.searchDocumentsByTitle(title);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<DocumentResponse> updateDocument(@PathVariable Long id, @Valid @RequestBody DocumentUpdateRequest request) {
        log.info("Updating document with ID: {}", id);
        DocumentResponse response = documentService.updateDocument(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        log.info("Deleting document with ID: {}", id);
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}


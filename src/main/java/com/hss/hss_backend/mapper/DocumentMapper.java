package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.request.DocumentCreateRequest;
import com.hss.hss_backend.dto.request.DocumentUpdateRequest;
import com.hss.hss_backend.dto.response.DocumentResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Document;
import com.hss.hss_backend.entity.Owner;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentMapper {

    public static Document toEntity(DocumentCreateRequest request, Owner owner, Animal animal) {
        return Document.builder()
                .owner(owner)
                .animal(animal)
                .title(request.getTitle())
                .content(request.getContent())
                .documentType(request.getDocumentType() != null ? request.getDocumentType() : Document.DocumentType.GENERAL)
                .fileUrl(request.getFileUrl())
                .fileName(request.getFileName())
                .fileSize(request.getFileSize())
                .mimeType(request.getMimeType())
                .date(request.getDate())
                .isArchived(request.getIsArchived() != null ? request.getIsArchived() : false)
                .build();
    }

    public static void updateEntity(Document document, DocumentUpdateRequest request) {
        if (request.getTitle() != null) {
            document.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            document.setContent(request.getContent());
        }
        if (request.getDocumentType() != null) {
            document.setDocumentType(request.getDocumentType());
        }
        if (request.getFileUrl() != null) {
            document.setFileUrl(request.getFileUrl());
        }
        if (request.getFileName() != null) {
            document.setFileName(request.getFileName());
        }
        if (request.getFileSize() != null) {
            document.setFileSize(request.getFileSize());
        }
        if (request.getMimeType() != null) {
            document.setMimeType(request.getMimeType());
        }
        if (request.getDate() != null) {
            document.setDate(request.getDate());
        }
        if (request.getIsArchived() != null) {
            document.setIsArchived(request.getIsArchived());
        }
    }

    public static DocumentResponse toResponse(Document document) {
        return DocumentResponse.builder()
                .documentId(document.getDocumentId())
                .ownerId(document.getOwner().getOwnerId())
                .ownerName(document.getOwner().getFirstName() + " " + document.getOwner().getLastName())
                .animalId(document.getAnimal().getAnimalId())
                .animalName(document.getAnimal().getName())
                .title(document.getTitle())
                .content(document.getContent())
                .documentType(document.getDocumentType())
                .fileUrl(document.getFileUrl())
                .fileName(document.getFileName())
                .fileSize(document.getFileSize())
                .mimeType(document.getMimeType())
                .date(document.getDate())
                .isArchived(document.getIsArchived())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    public static List<DocumentResponse> toResponseList(List<Document> documents) {
        return documents.stream()
                .map(DocumentMapper::toResponse)
                .collect(Collectors.toList());
    }
}


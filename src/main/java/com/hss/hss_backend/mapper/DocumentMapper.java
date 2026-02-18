package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.DocumentCreateDTO;
import com.hss.hss_backend.dto.DocumentResponseDTO;
import com.hss.hss_backend.dto.DocumentUpdateDTO;
import com.hss.hss_backend.dto.request.DocumentCreateRequest;
import com.hss.hss_backend.dto.request.DocumentUpdateRequest;
import com.hss.hss_backend.dto.response.DocumentResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Document;
import com.hss.hss_backend.entity.Owner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentMapper {

    public Document toEntity(DocumentCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        Document document = new Document();
        document.setDocumentType(dto.getDocumentType());
        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        if (dto.getOwnerId() != null) {
            document.setOwner(ownerIdToOwner(dto.getOwnerId()));
        }
        if (dto.getAnimalId() != null) {
            document.setAnimal(animalIdToAnimal(dto.getAnimalId()));
        }
        document.setDate(LocalDate.now());
        document.setIsArchived(false);
        return document;
    }

    public DocumentResponseDTO toResponseDTO(Document entity) {
        if (entity == null) {
            return null;
        }
        DocumentResponseDTO dto = new DocumentResponseDTO();
        dto.setDocumentId(entity.getDocumentId());
        dto.setDocumentType(entity.getDocumentType());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setFileUrl(entity.getFileUrl());
        dto.setFileName(entity.getFileName());
        dto.setFileSize(entity.getFileSize());
        dto.setMimeType(entity.getMimeType());
        dto.setDate(entity.getDate());
        dto.setIsArchived(entity.getIsArchived());
        if (entity.getOwner() != null) {
            dto.setOwnerId(entity.getOwner().getOwnerId());
            dto.setOwnerName(entity.getOwner().getFirstName() + " " + entity.getOwner().getLastName());
            dto.setOwnerEmail(entity.getOwner().getEmail());
        }
        if (entity.getAnimal() != null) {
            dto.setAnimalId(entity.getAnimal().getAnimalId());
            dto.setAnimalName(entity.getAnimal().getName());
            if (entity.getAnimal().getSpecies() != null) {
                dto.setAnimalSpecies(entity.getAnimal().getSpecies().getName());
            }
        }
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public void updateEntityFromDTO(DocumentUpdateDTO dto, Document entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getDocumentType() != null) {
            entity.setDocumentType(dto.getDocumentType());
        }
        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            entity.setContent(dto.getContent());
        }
    }

    public Document toEntity(DocumentCreateRequest request, Owner owner, Animal animal) {
        if (request == null) {
            return null;
        }
        Document document = new Document();
        document.setDocumentType(request.getDocumentType());
        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        document.setOwner(owner);
        document.setAnimal(animal);
        document.setDate(LocalDate.now());
        document.setIsArchived(false);
        return document;
    }

    public DocumentResponse toResponse(Document entity) {
        if (entity == null) {
            return null;
        }
        DocumentResponse response = new DocumentResponse();
        response.setDocumentId(entity.getDocumentId());
        response.setDocumentType(entity.getDocumentType());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setFileUrl(entity.getFileUrl());
        response.setFileName(entity.getFileName());
        response.setFileSize(entity.getFileSize());
        response.setMimeType(entity.getMimeType());
        response.setDate(entity.getDate());
        response.setIsArchived(entity.getIsArchived());
        if (entity.getOwner() != null) {
            response.setOwnerId(entity.getOwner().getOwnerId());
            response.setOwnerName(entity.getOwner().getFirstName() + " " + entity.getOwner().getLastName());
        }
        if (entity.getAnimal() != null) {
            response.setAnimalId(entity.getAnimal().getAnimalId());
            response.setAnimalName(entity.getAnimal().getName());
        }
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    public void updateEntity(Document entity, DocumentUpdateRequest request) {
        if (entity == null || request == null) {
            return;
        }
        if (request.getDocumentType() != null) {
            entity.setDocumentType(request.getDocumentType());
        }
        if (request.getTitle() != null) {
            entity.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            entity.setContent(request.getContent());
        }
    }

    public List<DocumentResponse> toResponseList(List<Document> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Owner ownerIdToOwner(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        Owner owner = new Owner();
        owner.setOwnerId(ownerId);
        return owner;
    }

    private Animal animalIdToAnimal(Long animalId) {
        if (animalId == null) {
            return null;
        }
        Animal animal = new Animal();
        animal.setAnimalId(animalId);
        return animal;
    }
}

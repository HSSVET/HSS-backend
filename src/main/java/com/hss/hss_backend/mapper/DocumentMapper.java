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

import java.util.List;

public interface DocumentMapper {

    Document toEntity(DocumentCreateDTO dto);

    DocumentResponseDTO toResponseDTO(Document entity);

    void updateEntityFromDTO(DocumentUpdateDTO dto, Document entity);

    Document toEntity(DocumentCreateRequest request, Owner owner, Animal animal);

    DocumentResponse toResponse(Document entity);

    void updateEntity(Document entity, DocumentUpdateRequest request);

    List<DocumentResponse> toResponseList(List<Document> entities);

    // Helper methods expected by generated implementation
    default Owner ownerIdToOwner(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        Owner owner = new Owner();
        owner.setOwnerId(ownerId);
        return owner;
    }

    default Animal animalIdToAnimal(Long animalId) {
        if (animalId == null) {
            return null;
        }
        Animal animal = new Animal();
        animal.setAnimalId(animalId);
        return animal;
    }
}



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
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "documentId", ignore = true)
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    @Mapping(target = "mimeType", ignore = true)
    @Mapping(target = "date", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "isArchived", constant = "false")
    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "ownerIdToOwner")
    @Mapping(target = "animal", source = "animalId", qualifiedByName = "animalIdToAnimal")
    Document toEntity(DocumentCreateDTO dto);

    @Mapping(target = "ownerId", source = "owner.ownerId")
    @Mapping(target = "ownerName", source = "owner.firstName")
    @Mapping(target = "ownerEmail", source = "owner.email")
    @Mapping(target = "animalId", source = "animal.animalId")
    @Mapping(target = "animalName", source = "animal.name")
    @Mapping(target = "animalSpecies", source = "animal.species.name")
    DocumentResponseDTO toResponseDTO(Document entity);

    @Mapping(target = "documentId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "animal", ignore = true)
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    @Mapping(target = "mimeType", ignore = true)
    @Mapping(target = "date", ignore = true)
    void updateEntityFromDTO(DocumentUpdateDTO dto, @MappingTarget Document entity);

    // DocumentServiceImpl için gerekli metodlar
    @Mapping(target = "documentId", ignore = true)
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    @Mapping(target = "mimeType", ignore = true)
    @Mapping(target = "date", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "isArchived", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Document toEntity(DocumentCreateRequest request, Owner owner, Animal animal);

    @Mapping(target = "ownerId", source = "owner.ownerId")
    @Mapping(target = "ownerName", source = "owner.firstName")
    @Mapping(target = "animalId", source = "animal.animalId")
    @Mapping(target = "animalName", source = "animal.name")
    DocumentResponse toResponse(Document entity);

    @Mapping(target = "documentId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "animal", ignore = true)
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    @Mapping(target = "mimeType", ignore = true)
    @Mapping(target = "date", ignore = true)
    void updateEntity(@MappingTarget Document entity, DocumentUpdateRequest request);

    List<DocumentResponse> toResponseList(List<Document> entities);

    @Named("ownerIdToOwner")
    default Owner ownerIdToOwner(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        Owner owner = new Owner();
        owner.setOwnerId(ownerId);
        return owner;
    }

    @Named("animalIdToAnimal")
    default Animal animalIdToAnimal(Long animalId) {
        if (animalId == null) {
            return null;
        }
        Animal animal = new Animal();
        animal.setAnimalId(animalId);
        return animal;
    }
}
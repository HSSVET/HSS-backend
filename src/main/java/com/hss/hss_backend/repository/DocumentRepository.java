package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Find documents by owner with fetch join
    @Query("SELECT d FROM Document d JOIN FETCH d.owner JOIN FETCH d.animal WHERE d.owner.ownerId = :ownerId")
    List<Document> findByOwner_OwnerId(@Param("ownerId") Long ownerId);
    
    // Find documents by animal with fetch join
    @Query("SELECT d FROM Document d JOIN FETCH d.owner JOIN FETCH d.animal WHERE d.animal.animalId = :animalId")
    List<Document> findByAnimal_AnimalId(@Param("animalId") Long animalId);
    
    // Find documents by document type
    @Query("SELECT d FROM Document d JOIN FETCH d.owner JOIN FETCH d.animal WHERE d.documentType = :documentType")
    List<Document> findByDocumentType(@Param("documentType") Document.DocumentType documentType);
    
    // Find documents by owner and animal
    @Query("SELECT d FROM Document d JOIN FETCH d.owner JOIN FETCH d.animal WHERE d.owner.ownerId = :ownerId AND d.animal.animalId = :animalId")
    List<Document> findByOwner_OwnerIdAndAnimal_AnimalId(@Param("ownerId") Long ownerId, @Param("animalId") Long animalId);
    
    // Find documents by owner and document type
    @Query("SELECT d FROM Document d JOIN FETCH d.owner JOIN FETCH d.animal WHERE d.owner.ownerId = :ownerId AND d.documentType = :documentType")
    List<Document> findByOwner_OwnerIdAndDocumentType(@Param("ownerId") Long ownerId, @Param("documentType") Document.DocumentType documentType);
    
    // Find archived/non-archived documents
    @Query("SELECT d FROM Document d JOIN FETCH d.owner JOIN FETCH d.animal WHERE d.isArchived = :isArchived")
    List<Document> findByIsArchived(@Param("isArchived") Boolean isArchived);
    
    // Find documents by owner with pagination
    Page<Document> findByOwner_OwnerId(Long ownerId, Pageable pageable);
    
    // Find documents by animal with pagination
    Page<Document> findByAnimal_AnimalId(Long animalId, Pageable pageable);
    
    // Custom query for searching documents by title
    @Query("SELECT d FROM Document d JOIN FETCH d.owner JOIN FETCH d.animal WHERE d.title LIKE %:title%")
    List<Document> findByTitleContaining(@Param("title") String title);
    
    // Custom query for searching documents by owner name
    @Query("SELECT d FROM Document d JOIN FETCH d.owner JOIN FETCH d.animal WHERE d.owner.firstName LIKE %:name% OR d.owner.lastName LIKE %:name%")
    List<Document> findByOwnerNameContaining(@Param("name") String name);
    
    // Custom query for searching documents by animal name
    @Query("SELECT d FROM Document d JOIN FETCH d.owner JOIN FETCH d.animal WHERE d.animal.name LIKE %:name%")
    List<Document> findByAnimalNameContaining(@Param("name") String name);
}


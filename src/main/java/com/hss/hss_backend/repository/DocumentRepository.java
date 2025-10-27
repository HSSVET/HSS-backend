package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // ============================================
    // SINGLE DOCUMENT QUERIES (with eager loading)
    // ============================================
    
    @EntityGraph(attributePaths = {"owner", "animal"})
    Optional<Document> findById(Long id);

    // ============================================
    // OWNER BASED QUERIES
    // ============================================
    
    /**
     * Owner'a ait tüm dokümanları getir (N+1 problemi önlenir)
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE o.ownerId = :ownerId " +
           "ORDER BY d.createdAt DESC")
    List<Document> findByOwnerIdWithDetails(@Param("ownerId") Long ownerId);
    
    /**
     * Owner'a ait dokümanları sayfalama ile getir
     * NOT: EntityGraph ile N+1 problemi önlenir, JOIN FETCH pageable ile çalışmaz
     */
    @EntityGraph(attributePaths = {"owner", "animal"})
    Page<Document> findByOwner_OwnerId(Long ownerId, Pageable pageable);
    
    /**
     * Owner'a ait arşivlenmemiş dokümanlar
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE o.ownerId = :ownerId AND d.isArchived = false " +
           "ORDER BY d.createdAt DESC")
    List<Document> findActiveByOwnerId(@Param("ownerId") Long ownerId);
    
    /**
     * Owner'a ait arşivlenmemiş dokümanlar (pageable)
     */
    @EntityGraph(attributePaths = {"owner", "animal"})
    Page<Document> findByOwner_OwnerIdAndIsArchivedFalse(Long ownerId, Pageable pageable);

    // ============================================
    // ANIMAL BASED QUERIES
    // ============================================
    
    /**
     * Hayvana ait tüm dokümanlar
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE a.animalId = :animalId " +
           "ORDER BY d.createdAt DESC")
    List<Document> findByAnimalIdWithDetails(@Param("animalId") Long animalId);
    
    /**
     * Hayvana ait dokümanlar (pageable)
     */
    @EntityGraph(attributePaths = {"owner", "animal"})
    Page<Document> findByAnimal_AnimalId(Long animalId, Pageable pageable);
    
    /**
     * Hayvana ait arşivlenmemiş dokümanlar
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE a.animalId = :animalId AND d.isArchived = false " +
           "ORDER BY d.createdAt DESC")
    List<Document> findActiveByAnimalId(@Param("animalId") Long animalId);
    
    /**
     * Hayvana ait arşivlenmemiş dokümanlar (pageable)
     */
    @EntityGraph(attributePaths = {"owner", "animal"})
    Page<Document> findByAnimal_AnimalIdAndIsArchivedFalse(Long animalId, Pageable pageable);

    // ============================================
    // OWNER + ANIMAL COMBINED QUERIES
    // ============================================
    
    /**
     * Belirli owner ve hayvana ait dokümanlar
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE o.ownerId = :ownerId AND a.animalId = :animalId " +
           "ORDER BY d.createdAt DESC")
    List<Document> findByOwnerAndAnimal(@Param("ownerId") Long ownerId, 
                                        @Param("animalId") Long animalId);

    // ============================================
    // DOCUMENT TYPE QUERIES
    // ============================================
    
    /**
     * Doküman tipine göre getir
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE d.documentType = :documentType " +
           "ORDER BY d.createdAt DESC")
    List<Document> findByDocumentTypeWithDetails(@Param("documentType") Document.DocumentType documentType);
    
    /**
     * Owner ve doküman tipine göre
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE o.ownerId = :ownerId AND d.documentType = :documentType " +
           "ORDER BY d.createdAt DESC")
    List<Document> findByOwnerIdAndDocumentType(@Param("ownerId") Long ownerId, 
                                                @Param("documentType") Document.DocumentType documentType);
    
    /**
     * Hayvan ve doküman tipine göre
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE a.animalId = :animalId AND d.documentType = :documentType " +
           "ORDER BY d.createdAt DESC")
    List<Document> findByAnimalIdAndDocumentType(@Param("animalId") Long animalId, 
                                                 @Param("documentType") Document.DocumentType documentType);

    // ============================================
    // DATE RANGE QUERIES
    // ============================================
    
    /**
     * Tarih aralığında dokümanlar
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE d.date BETWEEN :startDate AND :endDate " +
           "ORDER BY d.date DESC")
    List<Document> findByDateRange(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);
    
    /**
     * Owner'a ait tarih aralığında dokümanlar
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE o.ownerId = :ownerId " +
           "AND d.date BETWEEN :startDate AND :endDate " +
           "ORDER BY d.date DESC")
    List<Document> findByOwnerIdAndDateRange(@Param("ownerId") Long ownerId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    // ============================================
    // SEARCH QUERIES
    // ============================================
    
    /**
     * Başlıkta arama (case-insensitive)
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
           "AND d.isArchived = false " +
           "ORDER BY d.createdAt DESC")
    List<Document> searchByTitle(@Param("title") String title);
    
    /**
     * Owner'a ait başlıkta arama
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE o.ownerId = :ownerId " +
           "AND LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
           "AND d.isArchived = false " +
           "ORDER BY d.createdAt DESC")
    List<Document> searchByOwnerIdAndTitle(@Param("ownerId") Long ownerId, 
                                          @Param("title") String title);
    
    /**
     * İçerikte arama (title veya content)
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE (LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND d.isArchived = false " +
           "ORDER BY d.createdAt DESC")
    List<Document> searchByKeyword(@Param("keyword") String keyword);

    // ============================================
    // FILE RELATED QUERIES
    // ============================================
    
    /**
     * Dosya ekli dokümanlar
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE d.fileUrl IS NOT NULL " +
           "ORDER BY d.createdAt DESC")
    List<Document> findDocumentsWithFiles();
    
    /**
     * Owner'a ait dosya ekli dokümanlar
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE o.ownerId = :ownerId AND d.fileUrl IS NOT NULL " +
           "ORDER BY d.createdAt DESC")
    List<Document> findDocumentsWithFilesByOwnerId(@Param("ownerId") Long ownerId);

    // ============================================
    // ARCHIVE QUERIES
    // ============================================
    
    /**
     * Arşivlenmemiş tüm dokümanlar
     */
    @EntityGraph(attributePaths = {"owner", "animal"})
    Page<Document> findByIsArchivedFalse(Pageable pageable);
    
    /**
     * Arşivlenmiş dokümanlar
     */
    @Query("SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.owner o " +
           "LEFT JOIN FETCH d.animal a " +
           "WHERE d.isArchived = true " +
           "ORDER BY d.updatedAt DESC")
    List<Document> findArchivedDocuments();

    // ============================================
    // COUNT QUERIES
    // ============================================
    
    /**
     * Owner'ın toplam doküman sayısı
     */
    long countByOwner_OwnerId(Long ownerId);
    
    /**
     * Hayvanın toplam doküman sayısı
     */
    long countByAnimal_AnimalId(Long animalId);
    
    /**
     * Owner'ın aktif doküman sayısı
     */
    long countByOwner_OwnerIdAndIsArchivedFalse(Long ownerId);
    
    /**
     * Hayvanın aktif doküman sayısı
     */
    long countByAnimal_AnimalIdAndIsArchivedFalse(Long animalId);
}
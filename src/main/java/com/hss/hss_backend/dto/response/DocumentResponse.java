package com.hss.hss_backend.dto.response;

import com.hss.hss_backend.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private Long ownerId;
    private String ownerName;
    private Long animalId;
    private String animalName;

    private Long documentId;
    private String title;
    private String content;
    private Document.DocumentType documentType;

    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String mimeType;

    private LocalDate date;
    private Boolean isArchived;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}



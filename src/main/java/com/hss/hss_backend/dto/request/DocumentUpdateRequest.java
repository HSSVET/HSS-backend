package com.hss.hss_backend.dto.request;

import com.hss.hss_backend.entity.Document;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateRequest {

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    private Document.DocumentType documentType;

    private String fileUrl;

    @Size(max = 255, message = "File name must not exceed 255 characters")
    private String fileName;

    private Long fileSize;

    @Size(max = 50, message = "Mime type must not exceed 50 characters")
    private String mimeType;

    private LocalDate date;

    private Boolean isArchived;
}


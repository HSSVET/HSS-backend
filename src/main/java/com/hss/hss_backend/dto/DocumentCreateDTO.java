package com.hss.hss_backend.dto;

import com.hss.hss_backend.entity.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;

    @NotNull(message = "Document type is required")
    private Document.DocumentType documentType;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @NotNull(message = "Animal ID is required")
    private Long animalId;
}

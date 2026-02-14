package com.hss.hss_backend.dto;

import com.hss.hss_backend.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateDTO {

    private String title;
    private String content;
    private Document.DocumentType documentType;
    private Boolean isArchived;
}



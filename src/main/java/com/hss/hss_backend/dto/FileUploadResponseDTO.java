package com.hss.hss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDTO {

    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private String signedUrl;
    private Long expirationTime;
    private Long documentId;
}

package com.hss.hss_backend.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String contentType;
    private Instant createdTime;
    private Instant updatedTime;
}

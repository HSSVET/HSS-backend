package com.hss.hss_backend.dto.response;

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
public class RadiologicalImagingResponse {

    private Long imageId;
    private Long animalId;
    private String animalName;
    private LocalDate date;
    private String type;
    private String imageUrl;
    private String comment;
    private Long fileSize;
    private String mimeType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

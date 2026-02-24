package com.hss.hss_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RadiologicalImagingCreateRequest {

    @NotNull(message = "Animal ID is required")
    private Long animalId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotBlank(message = "Type is required")
    @Size(max = 50, message = "Type must not exceed 50 characters")
    private String type;

    private String comment;

    private String imageUrl;

    private Long fileSize;

    @Size(max = 50, message = "MIME type must not exceed 50 characters")
    private String mimeType;
}

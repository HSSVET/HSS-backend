package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.request.RadiologicalImagingCreateRequest;
import com.hss.hss_backend.dto.response.RadiologicalImagingResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.RadiologicalImaging;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RadiologicalImagingMapper {

    public static RadiologicalImaging toEntity(RadiologicalImagingCreateRequest request, Animal animal) {
        return RadiologicalImaging.builder()
                .animal(animal)
                .date(request.getDate())
                .type(request.getType())
                .imageUrl(request.getImageUrl())
                .comment(request.getComment())
                .fileSize(request.getFileSize())
                .mimeType(request.getMimeType())
                .build();
    }

    public static RadiologicalImagingResponse toResponse(RadiologicalImaging imaging) {
        return RadiologicalImagingResponse.builder()
                .imageId(imaging.getImageId())
                .animalId(imaging.getAnimal().getAnimalId())
                .animalName(imaging.getAnimal().getName())
                .date(imaging.getDate())
                .type(imaging.getType())
                .imageUrl(imaging.getImageUrl())
                .comment(imaging.getComment())
                .fileSize(imaging.getFileSize())
                .mimeType(imaging.getMimeType())
                .createdAt(imaging.getCreatedAt())
                .updatedAt(imaging.getUpdatedAt())
                .build();
    }

    public static List<RadiologicalImagingResponse> toResponseList(List<RadiologicalImaging> imagings) {
        return imagings.stream()
                .map(RadiologicalImagingMapper::toResponse)
                .collect(Collectors.toList());
    }
}

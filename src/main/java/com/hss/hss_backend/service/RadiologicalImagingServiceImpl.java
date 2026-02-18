package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.RadiologicalImagingCreateRequest;
import com.hss.hss_backend.dto.response.RadiologicalImagingResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.RadiologicalImaging;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.RadiologicalImagingMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.RadiologicalImagingRepository;
import com.hss.hss_backend.security.ClinicContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RadiologicalImagingServiceImpl implements RadiologicalImagingService {

    private final RadiologicalImagingRepository radiologicalImagingRepository;
    private final AnimalRepository animalRepository;
    private final StorageService storageService;

    @Override
    public RadiologicalImagingResponse createRadiologicalImaging(RadiologicalImagingCreateRequest request, MultipartFile imageFile) {
        log.info("Creating radiological imaging for animal ID: {}", request.getAnimalId());

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal", request.getAnimalId()));

        validateClinicAccess(animal);

        Long clinicId = ClinicContext.getClinicId();
        if (clinicId == null) {
            throw new IllegalStateException("Clinic context is not set");
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileUrl = storageService.uploadFile(imageFile, "clinics/" + clinicId + "/radiology/" + animal.getAnimalId());
                request.setImageUrl(fileUrl);
                request.setFileSize(imageFile.getSize());
                request.setMimeType(imageFile.getContentType());
            } catch (IOException e) {
                log.error("Failed to upload radiological image file", e);
                throw new RuntimeException("Failed to upload image file", e);
            }
        }

        RadiologicalImaging imaging = RadiologicalImagingMapper.toEntity(request, animal);
        RadiologicalImaging savedImaging = radiologicalImagingRepository.save(imaging);

        log.info("Radiological imaging created successfully with ID: {}", savedImaging.getImageId());
        return RadiologicalImagingMapper.toResponse(savedImaging);
    }

    @Override
    @Transactional(readOnly = true)
    public RadiologicalImagingResponse getRadiologicalImagingById(Long imageId) {
        log.info("Fetching radiological imaging with ID: {}", imageId);

        RadiologicalImaging imaging = radiologicalImagingRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("RadiologicalImaging", imageId));

        validateClinicAccess(imaging.getAnimal());

        return RadiologicalImagingMapper.toResponse(imaging);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RadiologicalImagingResponse> getRadiologicalImagingsByAnimalId(Long animalId) {
        log.info("Fetching radiological imagings for animal ID: {}", animalId);

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", animalId));

        validateClinicAccess(animal);

        List<RadiologicalImaging> imagings = radiologicalImagingRepository.findByAnimal_AnimalIdOrderByDateDesc(animalId);
        return RadiologicalImagingMapper.toResponseList(imagings);
    }

    @Override
    public void deleteRadiologicalImaging(Long imageId) {
        log.info("Deleting radiological imaging with ID: {}", imageId);

        RadiologicalImaging imaging = radiologicalImagingRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("RadiologicalImaging", imageId));

        validateClinicAccess(imaging.getAnimal());

        if (imaging.getImageUrl() != null) {
            try {
                storageService.deleteFile(imaging.getImageUrl());
            } catch (Exception e) {
                log.warn("Failed to delete radiological image file from storage: {}", imaging.getImageUrl(), e);
            }
        }

        radiologicalImagingRepository.delete(imaging);
        log.info("Radiological imaging deleted successfully with ID: {}", imageId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByAnimalId(Long animalId) {
        return radiologicalImagingRepository.countByAnimal_AnimalId(animalId);
    }

    private void validateClinicAccess(Animal animal) {
        Long currentClinicId = ClinicContext.getClinicId();
        if (currentClinicId != null && animal.getOwner().getClinic() != null) {
            if (!currentClinicId.equals(animal.getOwner().getClinic().getClinicId())) {
                throw new AccessDeniedException("You do not have permission to access this animal's radiological imagings.");
            }
        }
    }
}

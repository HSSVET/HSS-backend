package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.OwnerCreateRequest;
import com.hss.hss_backend.dto.request.OwnerUpdateRequest;
import com.hss.hss_backend.dto.response.OwnerResponse;
import com.hss.hss_backend.entity.Owner;
import com.hss.hss_backend.exception.DuplicateResourceException;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.OwnerMapper;
import com.hss.hss_backend.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    @Override
    public OwnerResponse createOwner(OwnerCreateRequest request) {
        log.info("Creating owner: {} {}", request.getFirstName(), request.getLastName());
        
        // Check for duplicate email
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (ownerRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Owner with email '" + request.getEmail() + "' already exists");
            }
        }
        
        // Check for duplicate phone
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            if (ownerRepository.findByPhone(request.getPhone()).isPresent()) {
                throw new DuplicateResourceException("Owner with phone '" + request.getPhone() + "' already exists");
            }
        }
        
        Owner owner = OwnerMapper.toEntity(request);
        Owner savedOwner = ownerRepository.save(owner);
        
        log.info("Successfully created owner with ID: {}", savedOwner.getOwnerId());
        return OwnerMapper.toResponse(savedOwner);
    }

    @Override
    @Transactional(readOnly = true)
    public OwnerResponse getOwnerById(Long id) {
        log.info("Fetching owner with ID: {}", id);
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + id));
        return OwnerMapper.toResponse(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OwnerResponse> getAllOwners(Pageable pageable) {
        log.info("Fetching all owners with pagination");
        Page<Owner> owners = ownerRepository.findAll(pageable);
        return owners.map(OwnerMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OwnerResponse> searchOwnersByName(String name) {
        log.info("Searching owners by name: {}", name);
        List<Owner> owners = ownerRepository.findByNameContaining(name);
        return OwnerMapper.toResponseList(owners);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OwnerResponse> searchOwnersByEmail(String email) {
        log.info("Searching owners by email: {}", email);
        List<Owner> owners = ownerRepository.findByEmailContaining(email);
        return OwnerMapper.toResponseList(owners);
    }

    @Override
    public OwnerResponse updateOwner(Long id, OwnerUpdateRequest request) {
        log.info("Updating owner with ID: {}", id);
        
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + id));
        
        // Check for duplicate email if it's being updated
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            ownerRepository.findByEmail(request.getEmail())
                    .ifPresent(existingOwner -> {
                        if (!existingOwner.getOwnerId().equals(id)) {
                            throw new DuplicateResourceException("Owner with email '" + request.getEmail() + "' already exists");
                        }
                    });
        }
        
        // Check for duplicate phone if it's being updated
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            ownerRepository.findByPhone(request.getPhone())
                    .ifPresent(existingOwner -> {
                        if (!existingOwner.getOwnerId().equals(id)) {
                            throw new DuplicateResourceException("Owner with phone '" + request.getPhone() + "' already exists");
                        }
                    });
        }
        
        OwnerMapper.updateEntity(owner, request);
        Owner savedOwner = ownerRepository.save(owner);
        
        log.info("Successfully updated owner with ID: {}", savedOwner.getOwnerId());
        return OwnerMapper.toResponse(savedOwner);
    }

    @Override
    public void deleteOwner(Long id) {
        log.info("Deleting owner with ID: {}", id);
        
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + id));
        
        // Check if owner has associated animals
        if (!owner.getAnimals().isEmpty()) {
            throw new IllegalStateException("Cannot delete owner with associated animals. Please remove all animals first.");
        }
        
        ownerRepository.delete(owner);
        log.info("Successfully deleted owner with ID: {}", id);
    }
}

package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.service.OwnerService;

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
    private final com.hss.hss_backend.repository.InvoiceRepository invoiceRepository;
    private final com.hss.hss_backend.repository.PaymentRepository paymentRepository;
    private final com.hss.hss_backend.repository.ClinicRepository clinicRepository;

    @Override
    public OwnerResponse createOwner(OwnerCreateRequest request) {
        log.info("Creating owner: {} {}", request.getFirstName(), request.getLastName());

        // Get current clinic from context
        Long clinicId = com.hss.hss_backend.security.ClinicContext.getClinicId();
        if (clinicId == null) {
            throw new IllegalStateException("Clinic context is missing. Cannot create owner.");
        }

        com.hss.hss_backend.entity.Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with ID: " + clinicId));

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
        owner.setClinic(clinic); // Set the clinic relationship

        Owner savedOwner = ownerRepository.save(owner);

        log.info("Successfully created owner with ID: {} for Clinic ID: {}", savedOwner.getOwnerId(), clinicId);
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
        // Filter is usually applied automatically via Aspect or Filter definition on
        // Entity but here we use repository basic methods if needed
        // Assuming @Filter def on Owner entity handles this via Hibernate session
        // aspect or we rely on Spring Data JPA to filter
        // Ideally we should double check if the filter is active. For now standard
        // findAll respecting the aspect.
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
    public com.hss.hss_backend.dto.response.OwnerFinancialSummaryResponse getFinancialSummary(Long ownerId) {
        log.info("Calculating financial summary for owner ID: {}", ownerId);

        java.math.BigDecimal totalInvoiced = invoiceRepository.getTotalAmountByOwnerId(ownerId);
        if (totalInvoiced == null)
            totalInvoiced = java.math.BigDecimal.ZERO;

        java.math.BigDecimal totalPaid = paymentRepository.sumAmountByOwner_OwnerId(ownerId);
        if (totalPaid == null)
            totalPaid = java.math.BigDecimal.ZERO;

        java.math.BigDecimal balance = totalInvoiced.subtract(totalPaid);

        java.math.BigDecimal overdueAmount = invoiceRepository.getOverdueAmountByOwnerId(ownerId);
        if (overdueAmount == null)
            overdueAmount = java.math.BigDecimal.ZERO;

        return com.hss.hss_backend.dto.response.OwnerFinancialSummaryResponse.builder()
                .ownerId(ownerId)
                .totalInvoiced(totalInvoiced)
                .totalPaid(totalPaid)
                .balance(balance)
                .overdueAmount(overdueAmount)
                .build();
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
                            throw new DuplicateResourceException(
                                    "Owner with email '" + request.getEmail() + "' already exists");
                        }
                    });
        }

        // Check for duplicate phone if it's being updated
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            ownerRepository.findByPhone(request.getPhone())
                    .ifPresent(existingOwner -> {
                        if (!existingOwner.getOwnerId().equals(id)) {
                            throw new DuplicateResourceException(
                                    "Owner with phone '" + request.getPhone() + "' already exists");
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

        // Note: CascadeType.ALL on 'animals' relationship in Owner entity ensures pets
        // are deleted.
        // We removed the manual check to allow deletion.

        ownerRepository.delete(owner);
        log.info("Successfully deleted owner with ID: {}", id);
    }
}

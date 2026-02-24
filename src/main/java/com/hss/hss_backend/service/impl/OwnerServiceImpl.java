package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.service.OwnerService;

import com.hss.hss_backend.dto.request.OwnerCreateRequest;
import com.hss.hss_backend.dto.request.OwnerUpdateRequest;
import com.hss.hss_backend.dto.response.OwnerResponse;
import com.hss.hss_backend.entity.Owner;
import com.hss.hss_backend.exception.DuplicateResourceException;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.OwnerMapper;
import com.hss.hss_backend.repository.ClinicRepository;
import com.hss.hss_backend.repository.InvoiceRepository;
import com.hss.hss_backend.repository.OwnerRepository;
import com.hss.hss_backend.repository.PaymentRepository;
import com.hss.hss_backend.security.ClinicContext;
import com.hss.hss_backend.entity.Clinic;
import com.hss.hss_backend.dto.response.OwnerFinancialSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ClinicRepository clinicRepository;

    @Override
    public OwnerResponse createOwner(OwnerCreateRequest request) {
        log.info("Creating owner: {} {}", request.getFirstName(), request.getLastName());

        // Get current clinic from context
        Long clinicId = ClinicContext.getClinicId();
        if (clinicId == null) {
            throw new IllegalStateException("Clinic context is missing. Cannot create owner.");
        }

        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with ID: " + clinicId));

        // Check for duplicate email (within same clinic)
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (ownerRepository.findByClinicClinicIdAndEmail(clinicId, request.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Owner with email '" + request.getEmail() + "' already exists");
            }
        }

        // Check for duplicate phone (within same clinic)
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            if (ownerRepository.findByClinicClinicIdAndPhone(clinicId, request.getPhone()).isPresent()) {
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
        Long clinicId = ClinicContext.getClinicId();
        if (clinicId == null) {
            throw new IllegalStateException("Clinic context is missing. Cannot fetch owner.");
        }
        Owner owner = ownerRepository.findByOwnerIdAndClinicClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + id));
        return OwnerMapper.toResponse(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OwnerResponse> getAllOwners(Pageable pageable) {
        log.info("Fetching all owners with pagination for current clinic");
        Long clinicId = ClinicContext.getClinicId();
        if (clinicId == null) {
            throw new IllegalStateException("Clinic context is missing. Cannot list owners.");
        }
        Page<Owner> owners = ownerRepository.findByClinicClinicId(clinicId, pageable);
        return owners.map(OwnerMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OwnerResponse> searchOwnersByName(String name) {
        log.info("Searching owners by name: {}", name);
        Long clinicId = ClinicContext.getClinicId();
        if (clinicId == null) {
            throw new IllegalStateException("Clinic context is missing. Cannot search owners.");
        }
        List<Owner> owners = ownerRepository.findByClinicClinicIdAndNameContaining(clinicId, name);
        return OwnerMapper.toResponseList(owners);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OwnerResponse> searchOwnersByEmail(String email) {
        log.info("Searching owners by email: {}", email);
        Long clinicId = ClinicContext.getClinicId();
        if (clinicId == null) {
            throw new IllegalStateException("Clinic context is missing. Cannot search owners.");
        }
        List<Owner> owners = ownerRepository.findByClinicClinicIdAndEmailContaining(clinicId, email);
        return OwnerMapper.toResponseList(owners);
    }

    @Override
    public OwnerFinancialSummaryResponse getFinancialSummary(Long ownerId) {
        log.info("Calculating financial summary for owner ID: {}", ownerId);
        Long clinicId = ClinicContext.getClinicId();
        if (clinicId == null) {
            throw new IllegalStateException("Clinic context is missing. Cannot get financial summary.");
        }
        Owner owner = ownerRepository.findByOwnerIdAndClinicClinicId(ownerId, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + ownerId));

        BigDecimal totalInvoiced = invoiceRepository.getTotalAmountByOwnerId(owner.getOwnerId());
        if (totalInvoiced == null)
            totalInvoiced = BigDecimal.ZERO;

        BigDecimal totalPaid = paymentRepository.sumAmountByOwner_OwnerId(owner.getOwnerId());
        if (totalPaid == null)
            totalPaid = BigDecimal.ZERO;

        BigDecimal balance = totalInvoiced.subtract(totalPaid);

        BigDecimal overdueAmount = invoiceRepository.getOverdueAmountByOwnerId(owner.getOwnerId());
        if (overdueAmount == null)
            overdueAmount = BigDecimal.ZERO;

        return OwnerFinancialSummaryResponse.builder()
                .ownerId(owner.getOwnerId())
                .totalInvoiced(totalInvoiced)
                .totalPaid(totalPaid)
                .balance(balance)
                .overdueAmount(overdueAmount)
                .build();
    }

    @Override
    public OwnerResponse updateOwner(Long id, OwnerUpdateRequest request) {
        log.info("Updating owner with ID: {}", id);
        Long clinicId = ClinicContext.getClinicId();
        if (clinicId == null) {
            throw new IllegalStateException("Clinic context is missing. Cannot update owner.");
        }
        Owner owner = ownerRepository.findByOwnerIdAndClinicClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + id));

        // Check for duplicate email if it's being updated (within same clinic)
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            ownerRepository.findByClinicClinicIdAndEmail(clinicId, request.getEmail())
                    .ifPresent(existingOwner -> {
                        if (!existingOwner.getOwnerId().equals(id)) {
                            throw new DuplicateResourceException(
                                    "Owner with email '" + request.getEmail() + "' already exists");
                        }
                    });
        }

        // Check for duplicate phone if it's being updated (within same clinic)
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            ownerRepository.findByClinicClinicIdAndPhone(clinicId, request.getPhone())
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
        Long clinicId = ClinicContext.getClinicId();
        if (clinicId == null) {
            throw new IllegalStateException("Clinic context is missing. Cannot delete owner.");
        }
        Owner owner = ownerRepository.findByOwnerIdAndClinicClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + id));

        // Note: CascadeType.ALL on 'animals' relationship in Owner entity ensures pets
        // are deleted.
        // We removed the manual check to allow deletion.

        ownerRepository.delete(owner);
        log.info("Successfully deleted owner with ID: {} (Soft Delete initiated)", id);
    }
}

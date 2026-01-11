package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.request.OwnerCreateRequest;
import com.hss.hss_backend.dto.request.OwnerUpdateRequest;
import com.hss.hss_backend.dto.response.OwnerResponse;
import com.hss.hss_backend.entity.Owner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OwnerMapper {

    public static Owner toEntity(OwnerCreateRequest request) {
        Owner.OwnerType type = Owner.OwnerType.INDIVIDUAL;
        if (request.getType() != null) {
            try {
                type = Owner.OwnerType.valueOf(request.getType());
            } catch (IllegalArgumentException e) {
                // Default to INDIVIDUAL or handle error
            }
        }

        return Owner.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .type(type)
                .corporateName(request.getCorporateName())
                .taxNo(request.getTaxNo())
                .taxOffice(request.getTaxOffice())
                .notes(request.getNotes())
                .warnings(request.getWarnings())
                .build();
    }

    public static void updateEntity(Owner owner, OwnerUpdateRequest request) {
        if (request.getFirstName() != null) {
            owner.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            owner.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            owner.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            owner.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            owner.setAddress(request.getAddress());
        }
        if (request.getType() != null) {
            try {
                owner.setType(Owner.OwnerType.valueOf(request.getType()));
            } catch (IllegalArgumentException e) {
                // Ignore invalid type
            }
        }
        if (request.getCorporateName() != null) {
            owner.setCorporateName(request.getCorporateName());
        }
        if (request.getTaxNo() != null) {
            owner.setTaxNo(request.getTaxNo());
        }
        if (request.getTaxOffice() != null) {
            owner.setTaxOffice(request.getTaxOffice());
        }
        if (request.getNotes() != null) {
            owner.setNotes(request.getNotes());
        }
        if (request.getWarnings() != null) {
            owner.setWarnings(request.getWarnings());
        }
    }

    public static OwnerResponse toResponse(Owner owner) {
        return OwnerResponse.builder()
                .ownerId(owner.getOwnerId())
                .firstName(owner.getFirstName())
                .lastName(owner.getLastName())
                .phone(owner.getPhone())
                .email(owner.getEmail())
                .address(owner.getAddress())
                .type(owner.getType() != null ? owner.getType().name() : null)
                .corporateName(owner.getCorporateName())
                .taxNo(owner.getTaxNo())
                .taxOffice(owner.getTaxOffice())
                .notes(owner.getNotes())
                .warnings(owner.getWarnings())
                .createdAt(owner.getCreatedAt())
                .updatedAt(owner.getUpdatedAt())
                .build();
    }

    public static List<OwnerResponse> toResponseList(List<Owner> owners) {
        return owners.stream()
                .map(OwnerMapper::toResponse)
                .collect(Collectors.toList());
    }
}

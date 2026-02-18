package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.OwnerCreateRequest;
import com.hss.hss_backend.dto.request.OwnerUpdateRequest;
import com.hss.hss_backend.dto.response.OwnerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OwnerService {

    OwnerResponse createOwner(OwnerCreateRequest request);

    OwnerResponse getOwnerById(Long id);

    Page<OwnerResponse> getAllOwners(Pageable pageable);

    List<OwnerResponse> searchOwnersByName(String name);

    List<OwnerResponse> searchOwnersByEmail(String email);

    com.hss.hss_backend.dto.response.OwnerFinancialSummaryResponse getFinancialSummary(Long ownerId);

    OwnerResponse updateOwner(Long id, OwnerUpdateRequest request);

    void deleteOwner(Long id);
}

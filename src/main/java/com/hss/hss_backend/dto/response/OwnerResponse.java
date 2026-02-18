package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerResponse {

    private Long ownerId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private String type;
    private String corporateName;
    private String taxNo;
    private String taxOffice;
    private String notes;
    private String warnings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

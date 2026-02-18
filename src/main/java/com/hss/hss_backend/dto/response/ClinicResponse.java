package com.hss.hss_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClinicResponse {
  private Long clinicId;
  private String name;
  private String address;
  private String phone;
  private String email;

  private String licenseKey;
  private String licenseType;
  private java.time.LocalDate licenseStartDate;
  private java.time.LocalDate licenseEndDate;
  private String licenseStatus;
  private String settings;
}

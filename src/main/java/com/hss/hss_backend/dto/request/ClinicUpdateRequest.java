package com.hss.hss_backend.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ClinicUpdateRequest {
  private String name;
  private String address;
  private String phone;
  @Email
  private String email;
  private String licenseKey;
  private String settings;
}

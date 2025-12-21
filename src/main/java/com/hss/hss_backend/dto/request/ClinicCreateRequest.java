package com.hss.hss_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClinicCreateRequest {
  @NotBlank(message = "Name is required")
  private String name;

  private String address;

  private String phone;

  @Email(message = "Invalid email format")
  private String email;

  // Optional: Email for the initial admin user to be invited/created
  private String adminEmail;
  private String adminFirstName;
  private String adminLastName;

  // e.g. STRT, PRO, ENT
  private String licenseType;
}

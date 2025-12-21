package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "clinic")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Clinic extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "clinic_id")
  private Long clinicId;

  @NotBlank(message = "Clinic name is required")
  @Size(max = 100, message = "Clinic name must not exceed 100 characters")
  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Size(max = 500, message = "Address must not exceed 500 characters")
  @Column(name = "address", columnDefinition = "TEXT")
  private String address;

  @Size(max = 20, message = "Phone must not exceed 20 characters")
  @Column(name = "phone", length = 20)
  private String phone;

  @Column(name = "email", length = 100)
  private String email;

  @Size(max = 50, message = "License key must not exceed 50 characters")
  @Column(name = "license_key", length = 50, unique = true)
  private String licenseKey; // Semantic Key: HSS-2025-GOLD-XXXX

  @Column(name = "slug", unique = true)
  private String slug;

  @Column(name = "license_type")
  private String licenseType; // GOLD, PLATINUM, STARTER

  @Column(name = "license_start_date")
  private java.time.LocalDate licenseStartDate;

  @Column(name = "license_end_date")
  private java.time.LocalDate licenseEndDate;

  @Column(name = "license_status")
  private String licenseStatus; // ACTIVE, EXPIRED, SUSPENDED

  @Column(name = "settings", columnDefinition = "TEXT")
  private String settings;

  // Relations
  @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Staff> staffMembers;

  @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Owner> owners;
}

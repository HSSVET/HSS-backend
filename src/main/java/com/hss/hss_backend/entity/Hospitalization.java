package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hospitalization")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@org.hibernate.annotations.Filter(name = "clinicFilter", condition = "clinic_id = :clinicId")
public class Hospitalization extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "hospitalization_id")
  private Long hospitalizationId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "clinic_id", nullable = false)
  private Clinic clinic;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "animal_id", nullable = false)
  private Animal animal;

  @Column(name = "admission_date", nullable = false)
  private LocalDateTime admissionDate;

  @Column(name = "discharge_date")
  private LocalDateTime dischargeDate;

  @Column(length = 20)
  @Builder.Default
  private String status = "ACTIVE"; // ACTIVE, DISCHARGED, TRANSFERRED

  @Column(name = "primary_veterinarian", length = 100)
  private String primaryVeterinarian;

  @Column(name = "diagnosis_summary", columnDefinition = "TEXT")
  private String diagnosisSummary;

  @Column(name = "care_plan", columnDefinition = "TEXT")
  private String carePlan;

  @OneToMany(mappedBy = "hospitalization", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<HospitalizationLog> logs = new ArrayList<>();

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "created_by", length = 100)
  private String createdBy;

  @Column(name = "updated_by", length = 100)
  private String updatedBy;
}

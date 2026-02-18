package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "animal_conditions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AnimalCondition extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "animal_id", nullable = false)
  private Animal animal;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ConditionType type; // ALLERGY, CHRONIC_CONDITION

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  private Severity severity; // MILD, MODERATE, SEVERE

  @Column(name = "diagnosis_date")
  private LocalDate diagnosisDate;

  @Column(name = "diagnosed_by")
  private String diagnosedBy;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private ConditionStatus status = ConditionStatus.ACTIVE; // ACTIVE, CHECK_REQUIRED, RESOLVED

  @Column(columnDefinition = "TEXT")
  private String notes;

  public enum ConditionType {
    ALLERGY,
    CHRONIC_CONDITION
  }

  public enum Severity {
    MILD,
    MODERATE,
    SEVERE
  }

  public enum ConditionStatus {
    ACTIVE,
    MANAGED,
    RESOLVED
  }
}

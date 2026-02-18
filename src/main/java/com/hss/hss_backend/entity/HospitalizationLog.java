package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "hospitalization_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalizationLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "log_id")
  private Long logId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hospitalization_id", nullable = false)
  private Hospitalization hospitalization;

  @Column(name = "log_date")
  @Builder.Default
  private LocalDateTime logDate = LocalDateTime.now();

  @Column(columnDefinition = "TEXT")
  private String notes;

  // Using String for JSONB simplification in JPA for now, or could use a custom
  // type
  // Ideally map this to a class if structure is known, but String is safer for
  // "notes" style JSON
  @Column(name = "vital_signs", columnDefinition = "jsonb")
  private String vitalSigns;

  @Column(name = "entry_by", length = 100)
  private String entryBy;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}

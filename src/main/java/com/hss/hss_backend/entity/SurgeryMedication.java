package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "surgery_medication")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryMedication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "surgery_med_id")
  private Long surgeryMedId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "surgery_id", nullable = false)
  private Surgery surgery;

  @Column(name = "medicine_id")
  private Long medicineId;

  @Column(nullable = false)
  private Integer quantity;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}

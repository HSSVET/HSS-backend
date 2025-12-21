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
@Table(name = "surgery")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Surgery {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "surgery_id")
  private Long surgeryId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "animal_id", nullable = false)
  private Animal animal;

  @Column(name = "veterinarian_id")
  private Long veterinarianId;

  @Column(nullable = false)
  private LocalDateTime date;

  @Column(length = 20)
  @Builder.Default
  private String status = "PLANNED"; // PLANNED, IN_PROGRESS, COMPLETED, CANCELLED

  @Column(columnDefinition = "TEXT")
  private String notes;

  @Column(name = "pre_op_instructions", columnDefinition = "TEXT")
  private String preOpInstructions;

  @Column(name = "post_op_instructions", columnDefinition = "TEXT")
  private String postOpInstructions;

  @Column(name = "anesthesia_protocol", columnDefinition = "TEXT")
  private String anesthesiaProtocol;

  @Column(name = "anesthesia_consent")
  @Builder.Default
  private Boolean anesthesiaConsent = false;

  @OneToMany(mappedBy = "surgery", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<SurgeryMedication> medications = new ArrayList<>();

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

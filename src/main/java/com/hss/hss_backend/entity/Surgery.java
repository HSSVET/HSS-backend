package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@EqualsAndHashCode(callSuper = false)
@org.hibernate.annotations.Filter(name = "clinicFilter", condition = "clinic_id = :clinicId")
public class Surgery extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "surgery_id")
  private Long surgeryId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "clinic_id", nullable = false)
  private Clinic clinic;

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

  // Enhanced workflow fields
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "appointment_id")
  private Appointment appointment;

  @Column(name = "operation_room", length = 50)
  private String operationRoom;

  @Column(name = "fasting_hours")
  @Builder.Default
  private Integer fastingHours = 12;

  // Pre-operative fields
  @Column(name = "pre_op_exam_completed")
  @Builder.Default
  private Boolean preOpExamCompleted = false;

  @Column(name = "pre_op_exam_date")
  private LocalDateTime preOpExamDate;

  @Column(name = "pre_op_tests_completed")
  @Builder.Default
  private Boolean preOpTestsCompleted = false;

  @Column(name = "required_tests", columnDefinition = "TEXT")
  private String requiredTests; // JSON array

  // Consent tracking
  @Column(name = "anesthesia_consent_signed")
  @Builder.Default
  private Boolean anesthesiaConsentSigned = false;

  @Column(name = "anesthesia_consent_date")
  private LocalDateTime anesthesiaConsentDate;

  @Column(name = "surgery_consent_signed")
  @Builder.Default
  private Boolean surgeryConsentSigned = false;

  @Column(name = "surgery_consent_date")
  private LocalDateTime surgeryConsentDate;

  // Operative fields
  @Column(name = "actual_start_time")
  private LocalDateTime actualStartTime;

  @Column(name = "actual_end_time")
  private LocalDateTime actualEndTime;

  @Column(name = "complications", columnDefinition = "TEXT")
  private String complications;

  // Post-operative fields
  @Column(name = "discharge_type", length = 20)
  private String dischargeType; // SAME_DAY, HOSPITALIZATION, TRANSFER

  @Column(name = "discharge_date")
  private LocalDateTime dischargeDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follow_up_appointment_id")
  private Appointment followUpAppointment;

  @Column(name = "prescription_id")
  private Long prescriptionId;

  // SMS reminder tracking
  @Column(name = "pre_op_sms_sent")
  @Builder.Default
  private Boolean preOpSmsSent = false;

  @Column(name = "pre_op_sms_sent_at")
  private LocalDateTime preOpSmsSentAt;

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

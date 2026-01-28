package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "consent_form")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@org.hibernate.annotations.Filter(name = "clinicFilter", condition = "clinic_id = :clinicId")
public class ConsentForm extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "consent_form_id")
  private Long consentFormId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "clinic_id", nullable = false)
  private Clinic clinic;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private Owner owner;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "animal_id", nullable = false)
  private Animal animal;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "surgery_id")
  private Surgery surgery;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "appointment_id")
  private Appointment appointment;

  @Enumerated(EnumType.STRING)
  @Column(name = "form_type", nullable = false, length = 50)
  private FormType formType;

  @Column(name = "form_title", nullable = false, length = 200)
  private String formTitle;

  @Column(name = "form_content", nullable = false, columnDefinition = "TEXT")
  private String formContent;

  @Column(name = "signature_data", columnDefinition = "TEXT")
  private String signatureData;

  @Column(name = "signature_date")
  private LocalDateTime signatureDate;

  @Column(name = "signer_name", nullable = false, length = 200)
  private String signerName;

  @Column(name = "signer_relation", length = 50)
  private String signerRelation;

  @Column(name = "witness_name", length = 200)
  private String witnessName;

  @Column(name = "witness_signature_data", columnDefinition = "TEXT")
  private String witnessSignatureData;

  @Column(name = "witness_date")
  private LocalDateTime witnessDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  @Builder.Default
  private ConsentStatus status = ConsentStatus.PENDING;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @Column(name = "expiry_date")
  private LocalDate expiryDate;

  @Column(name = "document_url", columnDefinition = "TEXT")
  private String documentUrl;

  public enum FormType {
    ANESTHESIA, SURGERY, TREATMENT, EUTHANASIA, GENERAL, RESEARCH
  }

  public enum ConsentStatus {
    PENDING, SIGNED, DECLINED, EXPIRED, REVOKED
  }
}

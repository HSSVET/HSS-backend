package com.hss.hss_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "treatment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Treatment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "treatment_id")
    private Long treatmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    @NotNull(message = "Animal is required")
    @JsonIgnore
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    @NotNull(message = "Clinic is required")
    @JsonIgnore
    private Clinic clinic;

    @Enumerated(EnumType.STRING)
    @Column(name = "treatment_type", length = 50, nullable = false)
    @NotNull(message = "Treatment type is required")
    private TreatmentType treatmentType;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private TreatmentStatus status = TreatmentStatus.ONGOING;

    @Size(max = 100, message = "Veterinarian name must not exceed 100 characters")
    @Column(name = "veterinarian_name", length = 100)
    private String veterinarianName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @DecimalMin(value = "0.0", message = "Cost must be positive")
    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    public enum TreatmentType {
        MEDICATION,
        SURGERY,
        THERAPY,
        PROCEDURE,
        OTHER
    }

    public enum TreatmentStatus {
        ONGOING,
        COMPLETED,
        CANCELLED
    }
}

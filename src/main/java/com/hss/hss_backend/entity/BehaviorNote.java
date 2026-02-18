package com.hss.hss_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "behavior_note")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BehaviorNote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "behavior_note_id")
    private Long behaviorNoteId;

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
    @Column(name = "category", length = 50)
    private BehaviorCategory category;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 20)
    private Severity severity;

    @NotNull(message = "Observed date is required")
    @Column(name = "observed_date", nullable = false)
    private LocalDate observedDate;

    @Size(max = 100, message = "Observed by must not exceed 100 characters")
    @Column(name = "observed_by", length = 100)
    private String observedBy;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    public enum BehaviorCategory {
        AGGRESSION,
        ANXIETY,
        FEEDING,
        SOCIAL,
        TRAINING,
        OTHER
    }

    public enum Severity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}

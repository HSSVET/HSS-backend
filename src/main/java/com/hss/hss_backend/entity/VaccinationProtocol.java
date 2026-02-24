package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vaccination_protocol")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VaccinationProtocol extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "protocol_id")
    private Long protocolId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id", nullable = false)
    private Vaccine vaccine;

    @Column(name = "protocol_name", nullable = false, length = 100)
    private String protocolName;

    @Column(name = "first_dose_age_weeks", nullable = false)
    @Builder.Default
    private Integer firstDoseAgeWeeks = 0;

    @Column(name = "dose_interval_weeks", nullable = false)
    @Builder.Default
    private Integer doseIntervalWeeks = 4;

    @Column(name = "total_doses", nullable = false)
    @Builder.Default
    private Integer totalDoses = 1;

    @Column(name = "booster_interval_months")
    private Integer boosterIntervalMonths;

    @Column(name = "is_required")
    @Builder.Default
    private Boolean isRequired = true;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 0;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}


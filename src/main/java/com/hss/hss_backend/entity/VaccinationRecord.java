package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "vaccination_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VaccinationRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccination_record_id")
    private Long vaccinationRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id", nullable = false)
    private Vaccine vaccine;

    @Column(name = "vaccine_name", nullable = false, length = 100)
    private String vaccineName;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Column(name = "batch_number", length = 50)
    private String batchNumber;

    @Column(name = "veterinarian_name", length = 100)
    private String veterinarianName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @Column(name = "barcode", length = 100, unique = true)
    private String barcode;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(name = "pre_exam_completed")
    @Builder.Default
    private Boolean preExamCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_appointment_id")
    private Appointment nextAppointment;
}

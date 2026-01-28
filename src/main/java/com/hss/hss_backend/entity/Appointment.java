package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "appointment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@org.hibernate.annotations.Filter(name = "clinicFilter", condition = "clinic_id = :clinicId")
public class Appointment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "subject", columnDefinition = "TEXT")
    private String subject;

    @Column(name = "veterinarian_id")
    private Long veterinarianId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private Status status = Status.SCHEDULED;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", length = 20)
    @Builder.Default
    private AppointmentType appointmentType = AppointmentType.GENERAL_EXAM;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "queue_number")
    private Integer queueNumber;

    @Column(name = "estimated_start_time")
    private LocalDateTime estimatedStartTime;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reminder> reminders;

    public enum AppointmentType {
        GENERAL_EXAM, VACCINATION, SURGERY, FOLLOW_UP, EMERGENCY, LAB_RESULTS
    }

    public enum Status {
        SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW
    }
}

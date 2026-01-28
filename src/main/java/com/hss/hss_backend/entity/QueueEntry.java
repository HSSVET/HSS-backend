package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "queue_entry")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@org.hibernate.annotations.Filter(name = "clinicFilter", condition = "clinic_id = :clinicId")
public class QueueEntry extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "queue_entry_id")
  private Long queueEntryId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "clinic_id", nullable = false)
  private Clinic clinic;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "appointment_id")
  private Appointment appointment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "animal_id", nullable = false)
  private Animal animal;

  @Column(name = "queue_number", nullable = false)
  private Integer queueNumber;

  @Column(name = "queue_date", nullable = false)
  private java.time.LocalDate queueDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  @Builder.Default
  private QueueStatus status = QueueStatus.WAITING;

  @Enumerated(EnumType.STRING)
  @Column(name = "priority", nullable = false, length = 10)
  @Builder.Default
  private Priority priority = Priority.NORMAL;

  @Column(name = "check_in_time", nullable = false)
  @Builder.Default
  private LocalDateTime checkInTime = LocalDateTime.now();

  @Column(name = "started_time")
  private LocalDateTime startedTime;

  @Column(name = "completed_time")
  private LocalDateTime completedTime;

  @Column(name = "assigned_veterinarian_id")
  private Long assignedVeterinarianId;

  @Column(name = "assigned_room", length = 50)
  private String assignedRoom;

  @Column(name = "estimated_duration_minutes")
  @Builder.Default
  private Integer estimatedDurationMinutes = 30;

  @Column(name = "estimated_start_time")
  private LocalDateTime estimatedStartTime;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  public enum QueueStatus {
    WAITING, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW
  }

  public enum Priority {
    LOW, NORMAL, HIGH, URGENT, EMERGENCY
  }
}

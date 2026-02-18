package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "animal_weight_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AnimalWeightHistory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "weight_history_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "animal_id", nullable = false)
  private Animal animal;

  @NotNull(message = "Weight is required")
  @DecimalMin(value = "0.0", message = "Weight must be positive")
  @DecimalMax(value = "999.99", message = "Weight must not exceed 999.99")
  @Column(name = "weight", precision = 5, scale = 2, nullable = false)
  private BigDecimal weight;

  @NotNull(message = "Date is required")
  @Column(name = "measured_at", nullable = false)
  private LocalDate measuredAt;

  @Size(max = 500, message = "Note must not exceed 500 characters")
  @Column(name = "note", length = 500)
  private String note;
}

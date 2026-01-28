package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.QueueCheckInRequest;
import com.hss.hss_backend.dto.response.QueueEntryResponse;
import com.hss.hss_backend.entity.QueueEntry;
import com.hss.hss_backend.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
@Slf4j

public class QueueController {

  private final QueueService queueService;

  /**
   * Check in a patient with an existing appointment
   */
  @PostMapping("/check-in/appointment/{appointmentId}")
  public ResponseEntity<QueueEntryResponse> checkInWithAppointment(@PathVariable Long appointmentId) {
    log.info("REST request to check in patient with appointment: {}", appointmentId);
    QueueEntryResponse response = queueService.checkInWithAppointment(appointmentId);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Check in a walk-in patient without appointment
   */
  @PostMapping("/check-in/walk-in")
  public ResponseEntity<QueueEntryResponse> walkInCheckIn(@RequestBody QueueCheckInRequest request) {
    log.info("REST request for walk-in check-in: {}", request);
    QueueEntryResponse response = queueService.walkInCheckIn(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Get today's complete queue
   */
  @GetMapping("/today")
  public ResponseEntity<List<QueueEntryResponse>> getTodayQueue() {
    log.info("REST request to get today's queue");
    List<QueueEntryResponse> queue = queueService.getTodayQueue();
    return ResponseEntity.ok(queue);
  }

  /**
   * Get active queue (waiting + in progress)
   */
  @GetMapping("/active")
  public ResponseEntity<List<QueueEntryResponse>> getActiveQueue() {
    log.info("REST request to get active queue");
    List<QueueEntryResponse> queue = queueService.getActiveQueue();
    return ResponseEntity.ok(queue);
  }

  /**
   * Update queue entry status
   */
  @PutMapping("/{queueEntryId}/status")
  public ResponseEntity<QueueEntryResponse> updateStatus(
      @PathVariable Long queueEntryId,
      @RequestBody Map<String, String> statusUpdate) {
    log.info("REST request to update queue entry {} status to: {}",
        queueEntryId, statusUpdate.get("status"));

    String statusStr = statusUpdate.get("status");
    QueueEntry.QueueStatus status = QueueEntry.QueueStatus.valueOf(statusStr.toUpperCase());

    QueueEntryResponse response = queueService.updateQueueStatus(queueEntryId, status);
    return ResponseEntity.ok(response);
  }

  /**
   * Assign veterinarian to queue entry
   */
  @PutMapping("/{queueEntryId}/assign")
  public ResponseEntity<QueueEntryResponse> assignVeterinarian(
      @PathVariable Long queueEntryId,
      @RequestBody Map<String, Object> assignment) {
    log.info("REST request to assign veterinarian to queue entry: {}", queueEntryId);

    Long veterinarianId = Long.valueOf(assignment.get("veterinarianId").toString());
    String room = assignment.get("room") != null ? assignment.get("room").toString() : null;

    QueueEntryResponse response = queueService.assignVeterinarian(queueEntryId, veterinarianId, room);
    return ResponseEntity.ok(response);
  }

  /**
   * Get next patient for veterinarian
   */
  @GetMapping("/next/{veterinarianId}")
  public ResponseEntity<QueueEntryResponse> getNextPatient(@PathVariable Long veterinarianId) {
    log.info("REST request to get next patient for veterinarian: {}", veterinarianId);
    QueueEntryResponse response = queueService.getNextPatient(veterinarianId);

    if (response == null) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(response);
  }

  /**
   * Get estimated wait time for a queue entry
   */
  @GetMapping("/{queueEntryId}/wait-time")
  public ResponseEntity<Map<String, Integer>> getWaitTime(@PathVariable Long queueEntryId) {
    log.info("REST request to get wait time for queue entry: {}", queueEntryId);
    Integer waitMinutes = queueService.getEstimatedWaitMinutes(queueEntryId);
    return ResponseEntity.ok(Map.of("estimatedWaitMinutes", waitMinutes));
  }
}

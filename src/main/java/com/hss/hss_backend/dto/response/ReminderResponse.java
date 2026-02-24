package com.hss.hss_backend.dto.response;

import com.hss.hss_backend.entity.Reminder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderResponse {

    private Long reminderId;
    private Long appointmentId;
    private LocalDateTime sendTime;
    private Reminder.Channel channel;
    private Reminder.Status status;
    private String message;
    private String recipientEmail;
    private String recipientPhone;
    private LocalDateTime sentAt;
    private String errorMessage;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


package com.hss.hss_backend.dto.request;

import com.hss.hss_backend.entity.Reminder;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReminderCreateRequest {

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Send time is required")
    private LocalDateTime sendTime;

    @NotNull(message = "Channel is required")
    private Reminder.Channel channel;

    private String message;
    private String recipientEmail;
    private String recipientPhone;
}


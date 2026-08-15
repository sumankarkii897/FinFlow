package com.finflow.notification.dtos.request;

import com.finflow.enums.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRequest {

    @NotBlank(message = "Recipient is required")
    @Email(message = "Recipient must be a valid email address")
    private String recipient;

    @NotBlank(message = "Subject is required")
    private String subject;

//    @NotBlank(message = "Body is required.")
    private String body;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotBlank(message = "Template name is required")
    private String templateName;

    private Map<String, Object> templateVariables;
}

package com.finflow.notification.dtos.response;

import com.finflow.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;

    private String subject;

    private String recipient;

    private String body;

    private NotificationType type;

    private LocalDateTime createdAt;

    // for values/variables to be passed into email template to send
    private String templateName;

//    private Map<String, Object> templateVariables;
}

package com.finflow;

import com.finflow.auth_users.entity.User;
import com.finflow.enums.NotificationType;
import com.finflow.notification.dtos.request.NotificationRequest;
import com.finflow.notification.services.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NotificationTest {
    @Autowired
    private NotificationService notificationService;
    @Test
    public void notificationTest() {

        NotificationRequest request = NotificationRequest.builder()
                .recipient("emailaddress")
                .subject("Finflow")
                .body("Hello World!")
                .type(NotificationType.EMAIL)

                .build();
        notificationService.sendEmail(request,new User());

    }
}

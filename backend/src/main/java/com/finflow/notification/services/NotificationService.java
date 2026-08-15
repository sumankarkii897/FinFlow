package com.finflow.notification.services;

import com.finflow.auth_users.entity.User;
import com.finflow.notification.dtos.request.NotificationRequest;
import com.finflow.notification.dtos.response.NotificationResponse;

public interface NotificationService {
void sendEmail(NotificationRequest notificationRequest, User user);

}

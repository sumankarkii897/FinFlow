package com.finflow.notification.services;

import com.finflow.auth_users.entity.User;
import com.finflow.notification.dtos.request.NotificationRequest;


public interface NotificationService {
void sendEmail(NotificationRequest notificationRequest, User user);

}

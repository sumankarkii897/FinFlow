package com.finflow.notification.services.impl;

import com.finflow.auth_users.entity.User;
import com.finflow.enums.NotificationType;
import com.finflow.notification.dtos.request.NotificationRequest;

import com.finflow.notification.entity.Notification;
import com.finflow.notification.repository.NotificationRepository;
import com.finflow.notification.services.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendEmail(NotificationRequest notificationRequest, User user) {
try{
    MimeMessage mimeMessage= mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(
            mimeMessage,
            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
            StandardCharsets.UTF_8.name()
    );
    helper.setTo(notificationRequest.getRecipient());
    helper.setSubject(notificationRequest.getSubject());
// use template if provided
    String body;
    if(notificationRequest.getTemplateName() !=null
            && !notificationRequest.getTemplateName().isBlank()){
        Context context = new Context();
        context.setVariables(notificationRequest.getTemplateVariables());
//        String htmlContent = templateEngine.process(notificationRequest.getTemplateName(), context);
     body = templateEngine.process(notificationRequest.getTemplateName(), context);
        helper.setText(body, true);

    }
    else{
//        if no template send text body directly
        body = notificationRequest.getBody();
//        helper.setText(notificationRequest.getBody(), true);
        helper.setText(body, true);
    }
    mailSender.send(mimeMessage);
// Save to our db table
    Notification notificationToSave = Notification.builder()
            .recipient(notificationRequest.getRecipient())
            .subject(notificationRequest.getSubject())
//            .body(notificationRequest.getBody())
            .body(body)
            .type(NotificationType.EMAIL)
            .user(user)
            .build();

    notificationRepository.save(notificationToSave);

}
catch (MessagingException e){
    log.error(e.getMessage());
}
    }
}

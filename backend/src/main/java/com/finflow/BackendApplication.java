package com.finflow;

import com.finflow.auth_users.entity.User;
import com.finflow.enums.NotificationType;
import com.finflow.notification.dtos.request.NotificationRequest;
import com.finflow.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
//@RequiredArgsConstructor
public class BackendApplication {
//	private final NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
//	@Bean
//	CommandLineRunner runner(){
//		return args -> {
//			NotificationRequest request = NotificationRequest.builder()
//					.recipient("sumankarkii509@gmail.com")
//					.subject("Finflow test")
//					.body("Hello this is test email")
//					.type(NotificationType.EMAIL)
//					.build();
//
//			notificationService.sendEmail(request,new User());
//		};
//	}

}

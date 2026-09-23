package com.techshop.notification.application.service;

import com.techshop.notification.domain.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class NotificationService {

    private final List<Notification> notificationLog = new ArrayList<>();

    public void processNotification(String eventPayload) {
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .message("Order event processed: " + eventPayload)
                .timestamp(Instant.now())
                .build();

        notificationLog.add(notification);
        log.info("PROCESSED NOTIFICATION: {}", notification.getMessage());
    }

    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notificationLog);
    }
}
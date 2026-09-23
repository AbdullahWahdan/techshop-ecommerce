package com.techshop.notification.infrastructure.messaging;

import com.techshop.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "order-created-events", groupId = "notification-group")
    public void consumeOrderCreatedEvent(String eventPayload) {
        log.info("RECEIVED KAFKA EVENT from 'order-created-events': {}", eventPayload);
        notificationService.processNotification(eventPayload);
    }
}
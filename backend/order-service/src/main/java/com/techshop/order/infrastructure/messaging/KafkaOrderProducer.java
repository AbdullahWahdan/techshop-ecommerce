package com.techshop.order.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedEvent(Object eventPayload) {
        log.info("Publishing OrderCreatedEvent to Kafka topic 'order-created-events'");
        kafkaTemplate.send("order-created-events", eventPayload);
    }
}
package com.techshop.order.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "order_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private String eventType; // e.g. "ORDER_CREATED", "STOCK_RESERVED", "PAYMENT_SUCCESS", "ORDER_COMPLETED"

    @Column(columnDefinition = "TEXT")
    private String eventData; // JSON representation of the event payload

    @CreationTimestamp
    private Instant createdAt;
}
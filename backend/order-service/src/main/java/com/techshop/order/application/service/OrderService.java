package com.techshop.order.application.service;

import com.techshop.order.domain.model.Order;
import com.techshop.order.domain.model.OrderEvent;
import com.techshop.order.domain.model.OrderItem;
import com.techshop.order.domain.model.OrderStatus;
import com.techshop.order.domain.repository.OrderEventRepository;
import com.techshop.order.domain.repository.OrderRepository;
import com.techshop.order.infrastructure.messaging.KafkaOrderProducer;
import com.techshop.order.infrastructure.web.dto.OrderRequest;
import com.techshop.order.infrastructure.web.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventRepository orderEventRepository;
    private final KafkaOrderProducer kafkaOrderProducer;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 1. Calculate Total Amount
        BigDecimal totalAmount = request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Build Order Entity
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customerId(request.getCustomerId())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .items(request.getItems().stream().map(item -> OrderItem.builder()
                        .sku(item.getSku())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build()).collect(Collectors.toList()))
                .build();

        Order savedOrder = orderRepository.save(order);
        recordEvent(orderNumber, "ORDER_CREATED", "Order placed with total amount: " + totalAmount);

        // 3. SAGA STEP 1: Call Inventory Service to Deduct Stock
        boolean stockDeducted = deductInventoryStock(request);

        if (!stockDeducted) {
            savedOrder.setStatus(OrderStatus.FAILED);
            orderRepository.save(savedOrder);
            recordEvent(orderNumber, "ORDER_FAILED", "Insufficient stock in inventory");
            throw new IllegalStateException("Order creation failed due to insufficient stock");
        }

        recordEvent(orderNumber, "STOCK_RESERVED", "Stock reserved successfully");

        // 4. SAGA STEP 2: Mock Payment Gateway Process (Simulated Success)
        boolean paymentSuccess = processMockPayment(savedOrder);

        if (paymentSuccess) {
            savedOrder.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(savedOrder);
            recordEvent(orderNumber, "ORDER_COMPLETED", "Payment successful and order completed");

            // 5. Publish Event to Apache Kafka
            kafkaOrderProducer.sendOrderCreatedEvent(mapToResponse(savedOrder));
        } else {
            savedOrder.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(savedOrder);
            recordEvent(orderNumber, "ORDER_CANCELLED", "Payment failed");
        }

        return mapToResponse(savedOrder);
    }

    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));
        return mapToResponse(order);
    }

    public List<OrderEvent> getOrderHistory(String orderNumber) {
        return orderEventRepository.findByOrderNumberOrderByCreatedAtAsc(orderNumber);
    }

    private boolean deductInventoryStock(OrderRequest request) {
        try {
            for (var item : request.getItems()) {
                Map<String, Object> body = Map.of("sku", item.getSku(), "quantity", item.getQuantity());
                restTemplate.postForEntity("http://localhost:8082/api/v1/inventory/deduct", body, String.class);
            }
            return true;
        } catch (Exception e) {
            log.error("Failed to deduct inventory stock: {}", e.getMessage());
            return false;
        }
    }

    private boolean processMockPayment(Order order) {
        // Mock Payment Gateway: Always succeeds for test amounts > 0
        return order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    private void recordEvent(String orderNumber, String eventType, String data) {
        OrderEvent event = OrderEvent.builder()
                .orderNumber(orderNumber)
                .eventType(eventType)
                .eventData(data)
                .build();
        orderEventRepository.save(event);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(item -> OrderResponse.OrderItemResponse.builder()
                        .sku(item.getSku())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
package com.techshop.order.infrastructure.web;

import com.techshop.order.application.service.OrderService;
import com.techshop.order.domain.model.OrderEvent;
import com.techshop.order.infrastructure.web.dto.OrderRequest;
import com.techshop.order.infrastructure.web.dto.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        return new ResponseEntity<>(orderService.placeOrder(request), HttpStatus.CREATED);
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }

    @GetMapping("/{orderNumber}/history")
    public ResponseEntity<List<OrderEvent>> getOrderHistory(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderHistory(orderNumber));
    }
}
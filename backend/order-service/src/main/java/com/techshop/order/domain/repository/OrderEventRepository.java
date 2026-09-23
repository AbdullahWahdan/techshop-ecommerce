package com.techshop.order.domain.repository;

import com.techshop.order.domain.model.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {
    List<OrderEvent> findByOrderNumberOrderByCreatedAtAsc(String orderNumber);
}
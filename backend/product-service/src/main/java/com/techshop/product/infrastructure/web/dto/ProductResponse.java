package com.techshop.product.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private String category;
    private String brand;
    private Integer stockQuantity;
    private Map<String, String> attributes;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
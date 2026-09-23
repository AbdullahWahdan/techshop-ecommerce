package com.techshop.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {

    @Id
    private String id;
    
    private String name;
    private String description;
    private String sku; // Stock Keeping Unit (e.g., "APP-IPH15P-256")
    private BigDecimal price;
    private String category;
    private String brand;
    private Integer stockQuantity;
    
    // Dynamic technical specifications (e.g. {"RAM": "16GB", "Storage": "512GB SSD", "Color": "Space Gray"})
    private Map<String, String> attributes;
    
    private Boolean active;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
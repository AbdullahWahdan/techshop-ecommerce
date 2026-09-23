package com.techshop.inventory.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private String sku;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Long version;
    private Boolean inStock;
}
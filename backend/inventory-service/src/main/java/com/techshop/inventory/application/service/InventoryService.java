package com.techshop.inventory.application.service;

import com.techshop.inventory.domain.model.Inventory;
import com.techshop.inventory.domain.repository.InventoryRepository;
import com.techshop.inventory.infrastructure.web.dto.DeductStockRequest;
import com.techshop.inventory.infrastructure.web.dto.InventoryRequest;
import com.techshop.inventory.infrastructure.web.dto.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryResponse addOrUpdateStock(InventoryRequest request) {
        Inventory inventory = inventoryRepository.findBySku(request.getSku())
                .orElse(Inventory.builder()
                        .sku(request.getSku())
                        .availableQuantity(0)
                        .reservedQuantity(0)
                        .build());

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + request.getQuantity());
        Inventory saved = inventoryRepository.save(inventory);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getStockBySku(String sku) {
        Inventory inventory = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Inventory record not found for SKU: " + sku));
        return mapToResponse(inventory);
    }

    @Transactional
    public InventoryResponse deductStock(DeductStockRequest request) {
        Inventory inventory = inventoryRepository.findBySku(request.getSku())
                .orElseThrow(() -> new RuntimeException("Inventory record not found for SKU: " + request.getSku()));

        if (inventory.getAvailableQuantity() < request.getQuantity()) {
            throw new IllegalStateException("Insufficient stock for SKU: " + request.getSku() 
                    + ". Available: " + inventory.getAvailableQuantity() 
                    + ", Requested: " + request.getQuantity());
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - request.getQuantity());
        Inventory updated = inventoryRepository.save(inventory);
        return mapToResponse(updated);
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .sku(inventory.getSku())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .version(inventory.getVersion())
                .inStock(inventory.getAvailableQuantity() > 0)
                .build();
    }
}
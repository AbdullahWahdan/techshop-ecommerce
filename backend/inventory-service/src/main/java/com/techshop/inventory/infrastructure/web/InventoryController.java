package com.techshop.inventory.infrastructure.web;

import com.techshop.inventory.application.service.InventoryService;
import com.techshop.inventory.infrastructure.web.dto.DeductStockRequest;
import com.techshop.inventory.infrastructure.web.dto.InventoryRequest;
import com.techshop.inventory.infrastructure.web.dto.InventoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/add")
    public ResponseEntity<InventoryResponse> addStock(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.addOrUpdateStock(request));
    }

    @GetMapping("/{sku}")
    public ResponseEntity<InventoryResponse> getStock(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.getStockBySku(sku));
    }

    @PostMapping("/deduct")
    public ResponseEntity<InventoryResponse> deductStock(@Valid @RequestBody DeductStockRequest request) {
        return ResponseEntity.ok(inventoryService.deductStock(request));
    }
}
package com.techshop.inventory;

import com.techshop.inventory.application.service.InventoryService;
import com.techshop.inventory.infrastructure.web.dto.InventoryRequest;
import com.techshop.inventory.infrastructure.web.dto.InventoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class InventoryServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("inventory_db_test")
            .withUsername("postgres")
            .withPassword("testpassword");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private InventoryService inventoryService;

    @Test
    void testAddStockAndRetrieve() {
        InventoryRequest request = new InventoryRequest();
        request.setSku("TEST-SKU-001");
        request.setQuantity(50);

        InventoryResponse response = inventoryService.addOrUpdateStock(request);

        assertNotNull(response.getId());
        assertEquals("TEST-SKU-001", response.getSku());
        assertEquals(50, response.getAvailableQuantity());
        assertTrue(response.getInStock());
    }
}

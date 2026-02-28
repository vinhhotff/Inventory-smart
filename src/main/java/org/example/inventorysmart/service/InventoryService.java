package org.example.inventorysmart.service;

public interface InventoryService {
    void reserveStock(Long productId, Integer quantity);
}

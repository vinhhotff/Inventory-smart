package org.example.inventorysmart.service;

import org.springframework.transaction.annotation.Transactional;

public abstract class InventoryService {
    @Transactional
    public abstract void reserveStock(Long productId, Integer quantity);
}

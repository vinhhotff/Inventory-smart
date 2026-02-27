package org.example.inventorysmart.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.inventorysmart.entity.Inventory;
import org.example.inventorysmart.exception.AppException;
import org.example.inventorysmart.exception.ErrorCode;
import org.example.inventorysmart.repository.InventoryRepository;
import org.example.inventorysmart.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryServiceImpl extends InventoryService {

    InventoryRepository inventoryRepository;

    @Transactional
    @Override
    public void reserveStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);

        inventoryRepository.save(inventory);
    }
}

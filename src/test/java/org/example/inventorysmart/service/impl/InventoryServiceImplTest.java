package org.example.inventorysmart.service.impl;

import org.example.inventorysmart.entity.Inventory;
import org.example.inventorysmart.exception.AppException;
import org.example.inventorysmart.exception.ErrorCode;
import org.example.inventorysmart.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory mockInventory;

    @BeforeEach
    void setUp() {
        mockInventory = Inventory.builder()
                .productId(1L)
                .availableQuantity(50)
                .reservedQuantity(10)
                .build();
    }

    @Test
    void reserveStock_Success_QuantityDeducted() {
        // Arrange
        Long productId = 1L;
        Integer quantityToReserve = 5;

        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(mockInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        inventoryService.reserveStock(productId, quantityToReserve);

        // Assert
        assertThat(mockInventory.getAvailableQuantity()).isEqualTo(45);
        assertThat(mockInventory.getReservedQuantity()).isEqualTo(15);
        verify(inventoryRepository, times(1)).save(mockInventory);
    }

    @Test
    void reserveStock_Fail_ProductNotFound() {
        // Arrange
        Long productId = 99L;
        Integer quantityToReserve = 5;

        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        Throwable thrown = catchThrowable(() -> inventoryService.reserveStock(productId, quantityToReserve));

        // Assert
        assertThat(thrown)
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());

        AppException appException = (AppException) thrown;
        assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void reserveStock_Fail_InsufficientStock() {
        // Arrange
        Long productId = 1L;
        Integer quantityToReserve = 100; // Greater than available (50)

        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(mockInventory));

        // Act
        Throwable thrown = catchThrowable(() -> inventoryService.reserveStock(productId, quantityToReserve));

        // Assert
        assertThat(thrown)
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INSUFFICIENT_STOCK.getMessage());

        AppException appException = (AppException) thrown;
        assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_STOCK);

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }
}

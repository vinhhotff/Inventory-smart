package org.example.inventorysmart.mapper;

import org.example.inventorysmart.dto.response.InventoryResponse;
import org.example.inventorysmart.entity.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    InventoryResponse toInventoryResponse(Inventory inventory);
}
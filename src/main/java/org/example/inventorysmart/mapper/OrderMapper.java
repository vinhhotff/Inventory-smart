package org.example.inventorysmart.mapper;

import org.example.inventorysmart.dto.request.OrderItemRequest;
import org.example.inventorysmart.dto.request.OrderRequest;
import org.example.inventorysmart.dto.response.OrderItemResponse;
import org.example.inventorysmart.dto.response.OrderResponse;
import org.example.inventorysmart.entity.Order;
import org.example.inventorysmart.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderItems", source = "items")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    Order toOrder(OrderRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "price", ignore = true)
    OrderItem toOrderItem(OrderItemRequest request);

    OrderResponse toOrderResponse(Order order);

    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}

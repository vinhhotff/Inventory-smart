package org.example.inventorysmart.service;

import org.example.inventorysmart.dto.request.OrderRequest;
import org.example.inventorysmart.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
}

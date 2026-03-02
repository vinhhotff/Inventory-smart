package org.example.inventorysmart.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.inventorysmart.dto.request.OrderRequest;
import org.example.inventorysmart.dto.response.OrderResponse;
import org.example.inventorysmart.entity.Order;
import org.example.inventorysmart.entity.OrderItem;
import org.example.inventorysmart.entity.OrderStatus;
import org.example.inventorysmart.mapper.OrderMapper;
import org.example.inventorysmart.producer.OrderProducer;
import org.example.inventorysmart.dto.event.OrderEvent;
import org.example.inventorysmart.repository.OrderRepository;
import org.example.inventorysmart.service.InventoryService;
import org.example.inventorysmart.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import org.example.inventorysmart.exception.AppException;
import org.example.inventorysmart.exception.ErrorCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;
    InventoryService inventoryService;
    OrderMapper orderMapper;
    OrderProducer orderProducer;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Order order = orderMapper.toOrder(request);
        order.setStatus(OrderStatus.PENDING);

        double totalAmount = 0.0;

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                inventoryService.reserveStock(item.getProductId(), item.getQuantity());

                double mockProductPrice = 500000.0;
                item.setPrice(mockProductPrice);
                totalAmount += mockProductPrice * item.getQuantity();
                item.setOrder(order);
            }
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .totalAmount(savedOrder.getTotalAmount())
                .build();
        orderProducer.sendOrderEvent(orderEvent);

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Cacheable(value = "orders", key = "#id")
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // CHỐNG IDOR: Nếu không phải chủ nhân đơn hàng VÀ không phải Admin -> Cấm!
        if (!order.getCreatedBy().equals(currentUsername) && !isAdmin) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return orderMapper.toOrderResponse(order);
    }
}

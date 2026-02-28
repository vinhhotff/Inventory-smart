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

import java.time.LocalDateTime;

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
        // 1. Ánh xạ từ DTO sang Order Entity
        Order order = orderMapper.toOrder(request);
        order.setStatus(OrderStatus.PENDING);

        double totalAmount = 0.0;

        // 2. Duyệt qua từng sản phẩm khách hàng order
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {

                // Gọi sang Module Kho (InventoryService) để giữ hàng
                // NẾU HẾT HÀNG: InventoryService tự quăng AppException,
                // @Transactional sẽ lập tức ROLLBACK (Hủy) ngay lập tức cả đơn hàng này.
                inventoryService.reserveStock(item.getProductId(), item.getQuantity());

                // (Lưu ý: Vì chưa có màn Quản lý Sản phẩm Product, giá đang được Mock tạm là
                // 500k)
                double mockProductPrice = 500000.0;
                item.setPrice(mockProductPrice);

                // Cộng dồn vào tổng tiền đơn hàng
                totalAmount += mockProductPrice * item.getQuantity();

                // Lắp ráp 2 chiều: Item phải biết nó thuộc về Order nào
                item.setOrder(order);
            }
        }

        order.setTotalAmount(totalAmount);

        // 3. Khối lệnh này sẽ TỰ ĐỘNG LƯU cả Order lẫn danh sách OrderItem
        // nhờ tính năng cascade = CascadeType.ALL đã cài trên Entity.
        Order savedOrder = orderRepository.save(order);

        // 4. Gửi OrderEvent vào RabbitMQ Message Queue
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .totalAmount(savedOrder.getTotalAmount())
                .build();
        orderProducer.sendOrderEvent(orderEvent);

        // 5. Đóng gói ra cái Đĩa đẹp đẽ (DTO) để trả về Frontend
        return orderMapper.toOrderResponse(savedOrder);
    }
}

package org.example.inventorysmart.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.inventorysmart.config.RabbitMQConfig;
import org.example.inventorysmart.dto.event.OrderEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleOrderEvent(OrderEvent orderEvent) {
        log.info("<< Nhận event từ RabbitMQ - Đang gửi email xác nhận cho đơn hàng ID: {} của User: {}",
                orderEvent.getOrderId(), orderEvent.getUserId());
        // Giả lập logic gửi Email / SMS ở đây
    }
}

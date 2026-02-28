package org.example.inventorysmart.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inventorysmart.config.RabbitMQConfig;
import org.example.inventorysmart.dto.event.OrderEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrderEvent(OrderEvent orderEvent) {
        log.info(">> Gửi event tạo đơn hàng vào RabbitMQ: {}", orderEvent);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                orderEvent);
    }
}

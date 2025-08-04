package com.fiap.pedido.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.usecase.ProcessOrderUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderConsumer {

    ObjectMapper objectMapper;
    ProcessOrderUseCase processOrderUseCase;


    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveOrder(String orderMessage) {
        log.info("Deserializing the order message: {}", orderMessage);
        try {
            Order order = objectMapper.readValue(orderMessage, Order.class);
            processOrderUseCase.execute(order);
        } catch (Exception e) {
            log.error("Error processing order message: {}", e.getMessage(), e);
        }
    }
}

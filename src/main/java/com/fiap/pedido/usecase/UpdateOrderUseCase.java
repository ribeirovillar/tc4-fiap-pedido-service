package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.gateway.OrderGateway;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UpdateOrderUseCase {

    OrderGateway orderGateway;

    @Transactional
    public Order execute(Order order) {
        log.info("Updating Order {}", order.toString());
        orderGateway.findOrderByOrderId(order.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return orderGateway.save(order)
                .orElseThrow(() -> new RuntimeException("Order could not be updated"));
    }

}

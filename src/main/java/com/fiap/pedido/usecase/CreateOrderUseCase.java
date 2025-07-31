package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.gateway.OrderGateway;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CreateOrderUseCase {

    OrderGateway orderGateway;
    @Transactional
    public Order execute(Order order) {
        if (Objects.isNull(order) || Objects.isNull(order.getOrderId())) {
            log.error("Order id is null or orderId is null");
            throw new RuntimeException("Order or Order ID cannot be null");
        }
        log.info("Creating order {}", order.toString());

        return orderGateway.save(order)
                .orElseThrow(() -> {
                            log.error("Order could not be saved: {}", order);
                            return new RuntimeException("Order could not be saved");
                        }
                );
    }

}

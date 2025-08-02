package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.OrderStatus;
import com.fiap.pedido.domain.PaymentStatus;
import com.fiap.pedido.exception.OrderException;
import com.fiap.pedido.gateway.OrderGateway;
import com.fiap.pedido.usecase.validation.ValidateOrderStrategy;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CreateOrderUseCase {

    private final OrderGateway orderGateway;
    private final List<ValidateOrderStrategy> validateOrderStrategies;

    public CreateOrderUseCase(OrderGateway orderGateway, List<ValidateOrderStrategy> validateOrderStrategies) {
        this.orderGateway = orderGateway;
        this.validateOrderStrategies = validateOrderStrategies;
    }

    @Transactional
    public Order execute(Order order) {
        validateOrderStrategies.forEach(strategy -> strategy.validate(order));

        order.setStatus(OrderStatus.ABERTO);
        order.setPaymentStatus(PaymentStatus.PENDING);

        log.info("Creating order {}", order);

        return orderGateway.save(order)
                .orElseThrow(() -> {
                            log.error("Order could not be saved: {}", order);
                            return new OrderException("Order could not be saved");
                        }
                );
    }

}

package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.exception.OrderNotFoundException;
import com.fiap.pedido.gateway.OrderGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RetrieveOrderByIdUseCase {

    private final OrderGateway gateway;

    public RetrieveOrderByIdUseCase(OrderGateway gateway) {
        this.gateway = gateway;
    }

    public Order execute(UUID orderId) {
        return gateway.findOrderByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

}

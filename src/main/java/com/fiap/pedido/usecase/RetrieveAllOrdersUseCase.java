package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.gateway.OrderGateway;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RetrieveAllOrdersUseCase {

    private final OrderGateway gateway;

    public RetrieveAllOrdersUseCase(OrderGateway gateway) {
        this.gateway = gateway;
    }

    public List<Order> execute() {
        return gateway.findAll();
    }

}

package com.fiap.pedido.gateway;

import com.fiap.pedido.domain.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderGateway {
    Optional<Order> save(Order order);

    Optional<Order> findOrderByOrderId(UUID orderId);

    List<Order> findAll();

}

package com.fiap.pedido.gateway;

import com.fiap.pedido.domain.Order;

import java.util.Optional;
import java.util.UUID;

public interface PaymentGateway {

    Optional<UUID> processPayment(Order order);

}

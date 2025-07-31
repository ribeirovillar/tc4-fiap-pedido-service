package com.fiap.pedido.gateway;

import com.fiap.pedido.domain.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerGateway {

    Optional<Customer> findById(UUID customerId);
}

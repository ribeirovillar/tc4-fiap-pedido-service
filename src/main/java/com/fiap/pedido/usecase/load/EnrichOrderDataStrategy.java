package com.fiap.pedido.usecase.load;

import com.fiap.pedido.domain.Order;

public interface EnrichOrderDataStrategy {
    void enrich(Order order);
}

package com.fiap.pedido.usecase.load;

import com.fiap.pedido.domain.Order;

public interface LoadOrderDataStrategy {
    void load(Order order);
}

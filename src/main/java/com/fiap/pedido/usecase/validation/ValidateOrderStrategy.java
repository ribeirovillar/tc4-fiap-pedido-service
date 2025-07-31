package com.fiap.pedido.usecase.validation;

import com.fiap.pedido.domain.Order;

public interface ValidateOrderStrategy {
    void execute(Order order);
}

package com.fiap.pedido.gateway;

import com.fiap.pedido.domain.Item;

import java.util.List;

public interface StockGateway {
    void deductStock(List<Item> items);
    void returnStock(List<Item> items);
}

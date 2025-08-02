package com.fiap.pedido.gateway.web;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.gateway.StockGateway;
import com.fiap.pedido.gateway.web.client.StockWebClient;
import com.fiap.pedido.mapper.OrderItemMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockWebClientGateway implements StockGateway {

    private final StockWebClient stockWebClient;
    private final OrderItemMapper orderItemMapper;

    public StockWebClientGateway(StockWebClient stockWebClient, OrderItemMapper orderItemMapper) {
        this.stockWebClient = stockWebClient;
        this.orderItemMapper = orderItemMapper;
    }

    public void deductStock(List<Item> items) {
        stockWebClient.deductStock(orderItemMapper.map(items));
    }

    public void returnStock(List<Item> items) {
        stockWebClient.returnStock(orderItemMapper.map(items));
    }
}

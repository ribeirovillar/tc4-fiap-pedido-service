package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.gateway.StockGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ReturnStockUseCase {

    private final StockGateway stockGateway;

    public ReturnStockUseCase(StockGateway stockGateway) {
        this.stockGateway = stockGateway;
    }

    public void execute(Order order) {
        List<Item> orderItems = order.getItems();
        log.info("Returning stock for items: {}", orderItems);
        try {
            stockGateway.returnStock(orderItems);
        } catch (Exception e) {
            log.error("Failed to return stock for items: {}", orderItems, e);
        }
    }

}

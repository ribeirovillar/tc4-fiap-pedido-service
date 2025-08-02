package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.exception.InsufficientStockException;
import com.fiap.pedido.gateway.StockGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@Slf4j
public class DeductStockUseCase {

    private final StockGateway stockGateway;

    public DeductStockUseCase(StockGateway stockGateway) {
        this.stockGateway = stockGateway;
    }

    public void execute(Order order) {
        log.info("Deducting stock for items: {}", order.getItems());
        try {
            stockGateway.deductStock(order.getItems());
        } catch (HttpClientErrorException.BadRequest e) {
            log.error("Failed to deduct stock for items: {}, due to bad request: {}", order.getItems(), e.getMessage(), e);
            throw new InsufficientStockException(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to deduct stock for items: {}", order.getItems(), e);
            throw e;
        }
    }

}

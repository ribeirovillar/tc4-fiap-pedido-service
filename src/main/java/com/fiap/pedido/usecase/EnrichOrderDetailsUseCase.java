package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.exception.DataEnrichmentException;
import com.fiap.pedido.gateway.OrderGateway;
import com.fiap.pedido.usecase.load.EnrichOrderDataStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class EnrichOrderDetailsUseCase {

    private final OrderGateway orderGateway;
    private final List<EnrichOrderDataStrategy> strategies;

    public EnrichOrderDetailsUseCase(OrderGateway orderGateway, List<EnrichOrderDataStrategy> strategies) {
        this.orderGateway = orderGateway;
        this.strategies = strategies;
    }

    public void execute(Order order) {
        try {
            strategies.forEach(strategy ->
                    strategy.enrich(order)
            );
            orderGateway.save(order)
                    .orElseThrow(() -> new DataEnrichmentException("Failed to update order with enriched data"));
        } catch (Exception e) {
            log.error("Failed to enrich order details for order {}: {}", order.getOrderId(), e.getMessage());
            throw new DataEnrichmentException("Failed to enrich order details", e);
        }
    }

}

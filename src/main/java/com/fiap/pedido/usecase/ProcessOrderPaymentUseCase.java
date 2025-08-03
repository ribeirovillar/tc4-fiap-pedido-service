package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.OrderStatus;
import com.fiap.pedido.domain.PaymentStatus;
import com.fiap.pedido.exception.OrderNotFoundException;
import com.fiap.pedido.exception.OrderStatusException;
import com.fiap.pedido.gateway.OrderGateway;
import com.fiap.pedido.gateway.PaymentGateway;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ProcessOrderPaymentUseCase {

    private final PaymentGateway paymentGateway;
    private final ReturnStockUseCase returnStockUseCase;
    private final OrderGateway orderGateway;

    public ProcessOrderPaymentUseCase(PaymentGateway paymentGateway, ReturnStockUseCase returnStockUseCase, OrderGateway orderGateway) {
        this.paymentGateway = paymentGateway;
        this.returnStockUseCase = returnStockUseCase;
        this.orderGateway = orderGateway;
    }

    @Transactional
    public void execute(UUID paymentId) {
        log.info("Processing payment for ID: {}", paymentId);

        PaymentStatus paymentStatus = paymentGateway.retrievePaymentStatus(paymentId);

        Order order = orderGateway.findOrderByPaymentId(paymentId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found for payment ID: " + paymentId));

        if (!OrderStatus.ABERTO.equals(order.getStatus())) {
            throw new OrderStatusException("Order is not in a valid state for payment processing: " + order.getStatus());
        }

        if (PaymentStatus.FAILED.equals(paymentStatus)) {
            returnStockUseCase.execute(order);
            order.setPaymentStatus(paymentStatus);
            order.setStatus(OrderStatus.FECHADO_SEM_CREDITO);
        } else {
            order.setPaymentStatus(paymentStatus);
            order.setStatus(OrderStatus.FECHADO_COM_SUCESSO);
        }
        orderGateway.save(order);
    }

}

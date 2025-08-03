package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.PaymentStatus;
import com.fiap.pedido.exception.InsufficientFundsException;
import com.fiap.pedido.exception.PaymentException;
import com.fiap.pedido.gateway.OrderGateway;
import com.fiap.pedido.gateway.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.UUID;

@Component
@Slf4j
public class InitPaymentUseCase {

    private final PaymentGateway paymentGateway;
    private final OrderGateway orderGateway;

    public InitPaymentUseCase(PaymentGateway paymentGateway, OrderGateway orderGateway) {
        this.paymentGateway = paymentGateway;
        this.orderGateway = orderGateway;
    }

    public void execute(Order order) {
        log.info("Processing payment for order: {}", order.getOrderId());
        try {
            UUID paymentId = paymentGateway.processPayment(order)
                    .orElseThrow(() -> new PaymentException(
                            "Missing payment identifier for order: " + order.getOrderId())
                    );
            order.setPaymentId(paymentId);
            order.setPaymentStatus(PaymentStatus.IN_PROGRESS);
        } catch (HttpClientErrorException.BadRequest e) {
            handlePaymentFailure(order, new InsufficientFundsException(e.getMessage()));
        } catch (Exception e) {
            handlePaymentFailure(order, new PaymentException(e.getMessage()));
        } finally {
            orderGateway.save(order);
        }

    }

    private void handlePaymentFailure(Order order, RuntimeException exception) {
        order.setPaymentStatus(PaymentStatus.FAILED);
        throw exception;
    }


}

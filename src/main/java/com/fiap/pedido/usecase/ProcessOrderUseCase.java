package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.OrderStatus;
import com.fiap.pedido.exception.InsufficientFundsException;
import com.fiap.pedido.exception.InsufficientStockException;
import com.fiap.pedido.exception.OrderException;
import com.fiap.pedido.exception.PaymentException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProcessOrderUseCase {

    CreateOrderUseCase createOrderUseCase;
    UpdateOrderUseCase updateOrderUseCase;
    DeductStockUseCase deductStockUseCase;
    ReturnStockUseCase returnStockUseCase;
    ProcessPaymentUseCase processPaymentUseCase;
    EnrichOrderDetailsUseCase enrichOrderDetailsUseCase;

    public void execute(Order order) {
        log.info("Processing order {}", order);

        try {
            createOrderUseCase.execute(order);
            enrichOrderDetailsUseCase.execute(order);
            deductStockUseCase.execute(order);
            processPaymentUseCase.execute(order);
        } catch (OrderException e) {
          log.error(e.getMessage());
        } catch (InsufficientStockException e) {
            handleError(order, OrderStatus.FECHADO_SEM_ESTOQUE, e.getMessage());
        } catch (InsufficientFundsException | PaymentException e) {
            returnStockUseCase.execute(order);
            handleError(order, OrderStatus.FECHADO_SEM_CREDITO, e.getMessage());
        } catch (Exception e) {
            handleError(order, OrderStatus.CANCELADO, e.getMessage());
        }
    }

    private void handleError(Order order, OrderStatus status, String errorMessage) {
        log.error("Error for order {}: {}", order.getOrderId(), errorMessage);
        order.setStatus(status);
        updateOrderUseCase.execute(order);
    }
}
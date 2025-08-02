package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.OrderStatus;
import com.fiap.pedido.domain.PaymentStatus;
import com.fiap.pedido.exception.OrderException;
import com.fiap.pedido.gateway.OrderGateway;
import com.fiap.pedido.usecase.validation.ValidateOrderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private ValidateOrderStrategy strategy1;

    @Mock
    private ValidateOrderStrategy strategy2;

    private CreateOrderUseCase createOrderUseCase;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        List<ValidateOrderStrategy> strategies = Arrays.asList(strategy1, strategy2);
        createOrderUseCase = new CreateOrderUseCase(orderGateway, strategies);

        testOrder = new Order();
        testOrder.setOrderId(UUID.randomUUID());
        testOrder.setCustomerId(UUID.randomUUID());
        testOrder.setCustomerName("John Doe");
        testOrder.setPaymentAmount(BigDecimal.valueOf(100.00));
    }

    @Test
    void execute_ShouldCreateOrderSuccessfully() {
        when(orderGateway.save(any(Order.class))).thenReturn(Optional.of(testOrder));

        Order result = createOrderUseCase.execute(testOrder);

        assertNotNull(result);
        assertEquals(OrderStatus.ABERTO, testOrder.getStatus());
        assertEquals(PaymentStatus.PENDING, testOrder.getPaymentStatus());
        verify(strategy1, times(1)).validate(testOrder);
        verify(strategy2, times(1)).validate(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_ShouldExecuteValidationStrategiesInOrder() {
        when(orderGateway.save(any(Order.class))).thenReturn(Optional.of(testOrder));

        createOrderUseCase.execute(testOrder);

        var inOrder = inOrder(strategy1, strategy2, orderGateway);
        inOrder.verify(strategy1).validate(testOrder);
        inOrder.verify(strategy2).validate(testOrder);
        inOrder.verify(orderGateway).save(testOrder);
    }

    @Test
    void execute_WithEmptyValidationStrategies_ShouldCreateOrder() {
        List<ValidateOrderStrategy> emptyStrategies = Collections.emptyList();
        createOrderUseCase = new CreateOrderUseCase(orderGateway, emptyStrategies);
        when(orderGateway.save(any(Order.class))).thenReturn(Optional.of(testOrder));

        Order result = createOrderUseCase.execute(testOrder);

        assertNotNull(result);
        assertEquals(OrderStatus.ABERTO, testOrder.getStatus());
        assertEquals(PaymentStatus.PENDING, testOrder.getPaymentStatus());
        verify(orderGateway, times(1)).save(testOrder);
        verifyNoInteractions(strategy1, strategy2);
    }

    @Test
    void execute_WhenValidationStrategyThrowsException_ShouldPropagateException() {
        RuntimeException validationException = new RuntimeException("Validation failed");
        doThrow(validationException).when(strategy1).validate(testOrder);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> createOrderUseCase.execute(testOrder));

        assertEquals("Validation failed", exception.getMessage());
        verify(strategy1, times(1)).validate(testOrder);
        verify(strategy2, never()).validate(testOrder);
        verify(orderGateway, never()).save(any(Order.class));
    }

    @Test
    void execute_WhenSecondValidationStrategyFails_ShouldStopProcessing() {
        RuntimeException validationException = new RuntimeException("Second validation failed");
        doNothing().when(strategy1).validate(testOrder);
        doThrow(validationException).when(strategy2).validate(testOrder);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> createOrderUseCase.execute(testOrder));

        assertEquals("Second validation failed", exception.getMessage());
        verify(strategy1, times(1)).validate(testOrder);
        verify(strategy2, times(1)).validate(testOrder);
        verify(orderGateway, never()).save(any(Order.class));
    }

    @Test
    void execute_WhenGatewaySaveReturnsEmpty_ShouldThrowOrderException() {
        when(orderGateway.save(any(Order.class))).thenReturn(Optional.empty());

        OrderException exception = assertThrows(OrderException.class,
                () -> createOrderUseCase.execute(testOrder));

        assertEquals("Order could not be saved", exception.getMessage());
        assertEquals(OrderStatus.ABERTO, testOrder.getStatus());
        assertEquals(PaymentStatus.PENDING, testOrder.getPaymentStatus());
        verify(strategy1, times(1)).validate(testOrder);
        verify(strategy2, times(1)).validate(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_WhenGatewayThrowsException_ShouldPropagateException() {
        RuntimeException gatewayException = new RuntimeException("Database error");
        when(orderGateway.save(any(Order.class))).thenThrow(gatewayException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> createOrderUseCase.execute(testOrder));

        assertEquals("Database error", exception.getMessage());
        verify(strategy1, times(1)).validate(testOrder);
        verify(strategy2, times(1)).validate(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_ShouldSetCorrectOrderStatus() {
        testOrder.setStatus(OrderStatus.FECHADO_COM_SUCESSO);
        when(orderGateway.save(any(Order.class))).thenReturn(Optional.of(testOrder));

        createOrderUseCase.execute(testOrder);

        assertEquals(OrderStatus.ABERTO, testOrder.getStatus());
        assertEquals(PaymentStatus.PENDING, testOrder.getPaymentStatus());
    }

    @Test
    void execute_ShouldOverrideExistingPaymentStatus() {
        testOrder.setPaymentStatus(PaymentStatus.IN_PROGRESS);
        when(orderGateway.save(any(Order.class))).thenReturn(Optional.of(testOrder));

        createOrderUseCase.execute(testOrder);

        assertEquals(PaymentStatus.PENDING, testOrder.getPaymentStatus());
    }

    @Test
    void execute_WithCompleteOrderData_ShouldPreserveFields() {
        testOrder.setCustomerCpf("12345678901");
        testOrder.setCardNumber("1234-5678-9012-3456");
        testOrder.setPaymentId(UUID.randomUUID());

        when(orderGateway.save(any(Order.class))).thenReturn(Optional.of(testOrder));

        Order result = createOrderUseCase.execute(testOrder);

        assertNotNull(result);
        assertEquals(testOrder.getCustomerCpf(), result.getCustomerCpf());
        assertEquals(testOrder.getCardNumber(), result.getCardNumber());
        assertEquals(testOrder.getPaymentId(), result.getPaymentId());
        assertEquals(testOrder.getPaymentAmount(), result.getPaymentAmount());
        assertEquals(OrderStatus.ABERTO, result.getStatus());
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());
    }
}

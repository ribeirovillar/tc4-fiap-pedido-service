package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.OrderStatus;
import com.fiap.pedido.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateOrderUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    private UpdateOrderUseCase updateOrderUseCase;

    private Order testOrder;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        updateOrderUseCase = new UpdateOrderUseCase(orderGateway);

        orderId = UUID.randomUUID();
        testOrder = new Order();
        testOrder.setOrderId(orderId);
        testOrder.setCustomerId(UUID.randomUUID());
        testOrder.setCustomerName("John Doe");
        testOrder.setStatus(OrderStatus.ABERTO);
        testOrder.setPaymentAmount(BigDecimal.valueOf(150.00));
    }

    @Test
    void execute_ShouldUpdateOrderSuccessfully() {
        when(orderGateway.findOrderByOrderId(orderId)).thenReturn(Optional.of(testOrder));
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        Order result = updateOrderUseCase.execute(testOrder);

        assertNotNull(result);
        assertEquals(testOrder.getOrderId(), result.getOrderId());
        assertEquals(testOrder.getCustomerName(), result.getCustomerName());
        verify(orderGateway, times(1)).findOrderByOrderId(orderId);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_WhenOrderNotFound_ShouldThrowRuntimeException() {
        when(orderGateway.findOrderByOrderId(orderId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> updateOrderUseCase.execute(testOrder));

        assertEquals("Order not found", exception.getMessage());
        verify(orderGateway, times(1)).findOrderByOrderId(orderId);
        verify(orderGateway, never()).save(any(Order.class));
    }

    @Test
    void execute_WhenSaveReturnsEmpty_ShouldThrowRuntimeException() {
        when(orderGateway.findOrderByOrderId(orderId)).thenReturn(Optional.of(testOrder));
        when(orderGateway.save(testOrder)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> updateOrderUseCase.execute(testOrder));

        assertEquals("Order could not be updated", exception.getMessage());
        verify(orderGateway, times(1)).findOrderByOrderId(orderId);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_WhenFindThrowsException_ShouldPropagateException() {
        RuntimeException databaseException = new RuntimeException("Database connection error");
        when(orderGateway.findOrderByOrderId(orderId)).thenThrow(databaseException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> updateOrderUseCase.execute(testOrder));

        assertEquals("Database connection error", exception.getMessage());
        verify(orderGateway, times(1)).findOrderByOrderId(orderId);
        verify(orderGateway, never()).save(any(Order.class));
    }

    @Test
    void execute_WhenSaveThrowsException_ShouldPropagateException() {
        RuntimeException saveException = new RuntimeException("Failed to save order");
        when(orderGateway.findOrderByOrderId(orderId)).thenReturn(Optional.of(testOrder));
        when(orderGateway.save(testOrder)).thenThrow(saveException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> updateOrderUseCase.execute(testOrder));

        assertEquals("Failed to save order", exception.getMessage());
        verify(orderGateway, times(1)).findOrderByOrderId(orderId);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_WithModifiedOrderData_ShouldUpdateCorrectly() {
        Order existingOrder = new Order();
        existingOrder.setOrderId(orderId);
        existingOrder.setCustomerName("Old Name");
        existingOrder.setStatus(OrderStatus.ABERTO);

        testOrder.setCustomerName("Updated Name");
        testOrder.setStatus(OrderStatus.FECHADO_COM_SUCESSO);

        when(orderGateway.findOrderByOrderId(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        Order result = updateOrderUseCase.execute(testOrder);

        assertNotNull(result);
        assertEquals("Updated Name", result.getCustomerName());
        assertEquals(OrderStatus.FECHADO_COM_SUCESSO, result.getStatus());
        verify(orderGateway, times(1)).findOrderByOrderId(orderId);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_WithNullOrderId_ShouldHandleGracefully() {
        testOrder.setOrderId(null);

        assertThrows(RuntimeException.class, () -> updateOrderUseCase.execute(testOrder));

        verify(orderGateway, times(1)).findOrderByOrderId(null);
    }

    @Test
    void execute_ShouldCallMethodsInCorrectOrder() {
        when(orderGateway.findOrderByOrderId(orderId)).thenReturn(Optional.of(testOrder));
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        updateOrderUseCase.execute(testOrder);

        var inOrder = inOrder(orderGateway);
        inOrder.verify(orderGateway).findOrderByOrderId(orderId);
        inOrder.verify(orderGateway).save(testOrder);
    }

    @Test
    void execute_WithDifferentOrderStatuses_ShouldUpdateSuccessfully() {
        for (OrderStatus status : OrderStatus.values()) {
            testOrder.setStatus(status);

            when(orderGateway.findOrderByOrderId(orderId)).thenReturn(Optional.of(testOrder));
            when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

            Order result = updateOrderUseCase.execute(testOrder);

            assertEquals(status, result.getStatus());

            reset(orderGateway);
        }
    }

    @Test
    void execute_WithCompleteOrderData_ShouldMaintainAllFields() {
        testOrder.setCustomerCpf("12345678901");
        testOrder.setCardNumber("1234-5678-9012-3456");
        testOrder.setPaymentId(UUID.randomUUID());

        when(orderGateway.findOrderByOrderId(orderId)).thenReturn(Optional.of(testOrder));
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        Order result = updateOrderUseCase.execute(testOrder);

        assertNotNull(result);
        assertEquals(testOrder.getCustomerCpf(), result.getCustomerCpf());
        assertEquals(testOrder.getCardNumber(), result.getCardNumber());
        assertEquals(testOrder.getPaymentId(), result.getPaymentId());
        assertEquals(testOrder.getPaymentAmount(), result.getPaymentAmount());
    }
}

package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.OrderStatus;
import com.fiap.pedido.exception.InsufficientFundsException;
import com.fiap.pedido.exception.InsufficientStockException;
import com.fiap.pedido.exception.OrderException;
import com.fiap.pedido.exception.PaymentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessOrderUseCaseTest {

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private UpdateOrderUseCase updateOrderUseCase;

    @Mock
    private DeductStockUseCase deductStockUseCase;

    @Mock
    private ReturnStockUseCase returnStockUseCase;

    @Mock
    private ProcessPaymentUseCase processPaymentUseCase;

    @Mock
    private EnrichOrderDetailsUseCase enrichOrderDetailsUseCase;

    private ProcessOrderUseCase processOrderUseCase;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        processOrderUseCase = new ProcessOrderUseCase(
                createOrderUseCase,
                updateOrderUseCase,
                deductStockUseCase,
                returnStockUseCase,
                processPaymentUseCase,
                enrichOrderDetailsUseCase
        );

        testOrder = new Order();
        testOrder.setOrderId(UUID.randomUUID());
        testOrder.setCustomerId(UUID.randomUUID());
        testOrder.setCustomerName("John Doe");
        testOrder.setPaymentAmount(BigDecimal.valueOf(100.00));
    }

    @Test
    void execute_ShouldProcessOrderSuccessfully() {
        when(createOrderUseCase.execute(testOrder)).thenReturn(testOrder);
        doNothing().when(enrichOrderDetailsUseCase).execute(testOrder);
        doNothing().when(deductStockUseCase).execute(testOrder);
        doNothing().when(processPaymentUseCase).execute(testOrder);

        assertDoesNotThrow(() -> processOrderUseCase.execute(testOrder));

        verify(createOrderUseCase, times(1)).execute(testOrder);
        verify(enrichOrderDetailsUseCase, times(1)).execute(testOrder);
        verify(deductStockUseCase, times(1)).execute(testOrder);
        verify(processPaymentUseCase, times(1)).execute(testOrder);
        verify(updateOrderUseCase, never()).execute(any(Order.class));
        verify(returnStockUseCase, never()).execute(any(Order.class));
    }

    @Test
    void execute_ShouldExecuteUseCasesInCorrectOrder() {
        when(createOrderUseCase.execute(testOrder)).thenReturn(testOrder);

        processOrderUseCase.execute(testOrder);

        var inOrder = inOrder(createOrderUseCase, enrichOrderDetailsUseCase, deductStockUseCase, processPaymentUseCase);
        inOrder.verify(createOrderUseCase).execute(testOrder);
        inOrder.verify(enrichOrderDetailsUseCase).execute(testOrder);
        inOrder.verify(deductStockUseCase).execute(testOrder);
        inOrder.verify(processPaymentUseCase).execute(testOrder);
    }

    @Test
    void execute_WhenOrderExceptionOccurs_ShouldLogErrorOnly() {
        OrderException orderException = new OrderException("Order creation failed");
        when(createOrderUseCase.execute(testOrder)).thenThrow(orderException);

        assertDoesNotThrow(() -> processOrderUseCase.execute(testOrder));

        verify(createOrderUseCase, times(1)).execute(testOrder);
        verify(enrichOrderDetailsUseCase, never()).execute(any(Order.class));
        verify(deductStockUseCase, never()).execute(any(Order.class));
        verify(processPaymentUseCase, never()).execute(any(Order.class));
        verify(updateOrderUseCase, never()).execute(any(Order.class));
    }

    @Test
    void execute_WhenInsufficientStockException_ShouldSetStatusToFechadoSemEstoque() {
        when(createOrderUseCase.execute(testOrder)).thenReturn(testOrder);
        doNothing().when(enrichOrderDetailsUseCase).execute(testOrder);
        doThrow(new InsufficientStockException("Insufficient stock")).when(deductStockUseCase).execute(testOrder);
        when(updateOrderUseCase.execute(testOrder)).thenReturn(testOrder);

        processOrderUseCase.execute(testOrder);

        assertEquals(OrderStatus.FECHADO_SEM_ESTOQUE, testOrder.getStatus());
        verify(createOrderUseCase, times(1)).execute(testOrder);
        verify(enrichOrderDetailsUseCase, times(1)).execute(testOrder);
        verify(deductStockUseCase, times(1)).execute(testOrder);
        verify(processPaymentUseCase, never()).execute(any(Order.class));
        verify(updateOrderUseCase, times(1)).execute(testOrder);
        verify(returnStockUseCase, never()).execute(any(Order.class));
    }

    @Test
    void execute_WhenInsufficientFundsException_ShouldReturnStockAndSetStatusToFechadoSemCredito() {
        when(createOrderUseCase.execute(testOrder)).thenReturn(testOrder);
        doNothing().when(enrichOrderDetailsUseCase).execute(testOrder);
        doNothing().when(deductStockUseCase).execute(testOrder);
        doThrow(new InsufficientFundsException("Insufficient funds")).when(processPaymentUseCase).execute(testOrder);
        doNothing().when(returnStockUseCase).execute(testOrder);
        when(updateOrderUseCase.execute(testOrder)).thenReturn(testOrder);

        processOrderUseCase.execute(testOrder);

        assertEquals(OrderStatus.FECHADO_SEM_CREDITO, testOrder.getStatus());
        verify(createOrderUseCase, times(1)).execute(testOrder);
        verify(enrichOrderDetailsUseCase, times(1)).execute(testOrder);
        verify(deductStockUseCase, times(1)).execute(testOrder);
        verify(processPaymentUseCase, times(1)).execute(testOrder);
        verify(returnStockUseCase, times(1)).execute(testOrder);
        verify(updateOrderUseCase, times(1)).execute(testOrder);
    }

    @Test
    void execute_WhenPaymentException_ShouldReturnStockAndSetStatusToFechadoSemCredito() {
        when(createOrderUseCase.execute(testOrder)).thenReturn(testOrder);
        doNothing().when(enrichOrderDetailsUseCase).execute(testOrder);
        doNothing().when(deductStockUseCase).execute(testOrder);
        doThrow(new PaymentException("Payment processing failed")).when(processPaymentUseCase).execute(testOrder);
        doNothing().when(returnStockUseCase).execute(testOrder);
        when(updateOrderUseCase.execute(testOrder)).thenReturn(testOrder);

        processOrderUseCase.execute(testOrder);

        assertEquals(OrderStatus.FECHADO_SEM_CREDITO, testOrder.getStatus());
        verify(returnStockUseCase, times(1)).execute(testOrder);
        verify(updateOrderUseCase, times(1)).execute(testOrder);
    }

    @Test
    void execute_WhenUnexpectedExceptionOccurs_ShouldSetStatusToCancelado() {
        when(createOrderUseCase.execute(testOrder)).thenReturn(testOrder);
        doNothing().when(enrichOrderDetailsUseCase).execute(testOrder);
        doThrow(new RuntimeException("Unexpected error")).when(deductStockUseCase).execute(testOrder);
        when(updateOrderUseCase.execute(testOrder)).thenReturn(testOrder);

        processOrderUseCase.execute(testOrder);

        assertEquals(OrderStatus.CANCELADO, testOrder.getStatus());
        verify(updateOrderUseCase, times(1)).execute(testOrder);
        verify(returnStockUseCase, never()).execute(any(Order.class));
    }

    @Test
    void execute_WhenEnrichOrderDetailsThrowsException_ShouldSetStatusToCancelado() {
        when(createOrderUseCase.execute(testOrder)).thenReturn(testOrder);
        doThrow(new RuntimeException("Enrichment failed")).when(enrichOrderDetailsUseCase).execute(testOrder);
        when(updateOrderUseCase.execute(testOrder)).thenReturn(testOrder);

        processOrderUseCase.execute(testOrder);

        assertEquals(OrderStatus.CANCELADO, testOrder.getStatus());
        verify(createOrderUseCase, times(1)).execute(testOrder);
        verify(enrichOrderDetailsUseCase, times(1)).execute(testOrder);
        verify(deductStockUseCase, never()).execute(any(Order.class));
        verify(processPaymentUseCase, never()).execute(any(Order.class));
        verify(updateOrderUseCase, times(1)).execute(testOrder);
    }

    @Test
    void execute_WhenMultipleExceptionsOccur_ShouldHandleFirstException() {
        when(createOrderUseCase.execute(testOrder)).thenReturn(testOrder);
        doNothing().when(enrichOrderDetailsUseCase).execute(testOrder);
        doThrow(new InsufficientStockException("Stock error")).when(deductStockUseCase).execute(testOrder);
        when(updateOrderUseCase.execute(testOrder)).thenReturn(testOrder);

        processOrderUseCase.execute(testOrder);

        assertEquals(OrderStatus.FECHADO_SEM_ESTOQUE, testOrder.getStatus());
        verify(processPaymentUseCase, never()).execute(any(Order.class));
    }
}

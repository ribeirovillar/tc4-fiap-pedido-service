package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.PaymentStatus;
import com.fiap.pedido.exception.InsufficientFundsException;
import com.fiap.pedido.exception.PaymentException;
import com.fiap.pedido.gateway.OrderGateway;
import com.fiap.pedido.gateway.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitPaymentUseCaseTest {

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private OrderGateway orderGateway;
    @Mock
    private HttpClientErrorException.BadRequest badRequestException;

    private InitPaymentUseCase initPaymentUseCase;

    private Order testOrder;
    private UUID paymentId;

    @BeforeEach
    void setUp() {
        initPaymentUseCase = new InitPaymentUseCase(paymentGateway, orderGateway);

        paymentId = UUID.randomUUID();
        testOrder = new Order();
        testOrder.setOrderId(UUID.randomUUID());
        testOrder.setCustomerId(UUID.randomUUID());
        testOrder.setCustomerName("John Doe");
        testOrder.setPaymentAmount(BigDecimal.valueOf(100.00));
        testOrder.setCardNumber("1234-5678-9012-3456");
    }

    @Test
    void execute_ShouldProcessPaymentSuccessfully() {
        when(paymentGateway.processPayment(testOrder)).thenReturn(Optional.of(paymentId));
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        assertDoesNotThrow(() -> initPaymentUseCase.execute(testOrder));

        assertEquals(paymentId, testOrder.getPaymentId());
        assertEquals(PaymentStatus.IN_PROGRESS, testOrder.getPaymentStatus());
        verify(paymentGateway, times(1)).processPayment(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_WhenPaymentGatewayReturnsEmpty_ShouldThrowPaymentException() {
        when(paymentGateway.processPayment(testOrder)).thenReturn(Optional.empty());
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        PaymentException exception = assertThrows(PaymentException.class,
                () -> initPaymentUseCase.execute(testOrder));

        assertTrue(exception.getMessage().contains("Missing payment identifier for order"));
        assertTrue(exception.getMessage().contains(testOrder.getOrderId().toString()));
        assertEquals(PaymentStatus.FAILED, testOrder.getPaymentStatus());
        verify(paymentGateway, times(1)).processPayment(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_WhenHttpClientErrorBadRequest_ShouldThrowInsufficientFundsException() {
        when(paymentGateway.processPayment(testOrder)).thenThrow(badRequestException);
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        assertThrows(InsufficientFundsException.class,
                () -> initPaymentUseCase.execute(testOrder));

        assertEquals(PaymentStatus.FAILED, testOrder.getPaymentStatus());
        assertNull(testOrder.getPaymentId());
        verify(paymentGateway, times(1)).processPayment(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_WhenGenericExceptionOccurs_ShouldThrowPaymentException() {
        RuntimeException genericException = new RuntimeException("Payment service unavailable");
        when(paymentGateway.processPayment(testOrder)).thenThrow(genericException);
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        PaymentException exception = assertThrows(PaymentException.class,
                () -> initPaymentUseCase.execute(testOrder));

        assertEquals("Payment service unavailable", exception.getMessage());
        assertEquals(PaymentStatus.FAILED, testOrder.getPaymentStatus());
        assertNull(testOrder.getPaymentId());
        verify(paymentGateway, times(1)).processPayment(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_ShouldAlwaysSaveOrderInFinally() {
        when(paymentGateway.processPayment(testOrder)).thenReturn(Optional.of(paymentId));
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        initPaymentUseCase.execute(testOrder);

        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_EvenWhenExceptionOccurs_ShouldStillSaveOrder() {
        when(paymentGateway.processPayment(testOrder)).thenThrow(new RuntimeException("Error"));
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        assertThrows(PaymentException.class, () -> initPaymentUseCase.execute(testOrder));

        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_WithExistingPaymentId_ShouldOverrideWithNewPaymentId() {
        UUID oldPaymentId = UUID.randomUUID();
        testOrder.setPaymentId(oldPaymentId);
        testOrder.setPaymentStatus(PaymentStatus.PENDING);

        when(paymentGateway.processPayment(testOrder)).thenReturn(Optional.of(paymentId));
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        initPaymentUseCase.execute(testOrder);

        assertEquals(paymentId, testOrder.getPaymentId());
        assertNotEquals(oldPaymentId, testOrder.getPaymentId());
        assertEquals(PaymentStatus.IN_PROGRESS, testOrder.getPaymentStatus());
    }

    @Test
    void execute_WhenOrderGatewayThrowsException_ShouldNotAffectPaymentProcessing() {
        when(paymentGateway.processPayment(testOrder)).thenReturn(Optional.of(paymentId));
        when(orderGateway.save(testOrder)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> initPaymentUseCase.execute(testOrder));

        assertEquals("Database error", exception.getMessage());
        assertEquals(paymentId, testOrder.getPaymentId());
        assertEquals(PaymentStatus.IN_PROGRESS, testOrder.getPaymentStatus());
        verify(paymentGateway, times(1)).processPayment(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }
}

package com.fiap.pedido.gateway.web;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.gateway.web.client.PaymentWebClient;
import com.fiap.pedido.gateway.web.json.PaymentDTO;
import com.fiap.pedido.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentWebClientGatewayTest {

    @Mock
    private PaymentWebClient paymentWebClient;

    @Mock
    private OrderMapper orderMapper;

    private PaymentWebClientGateway paymentWebClientGateway;

    private Order testOrder;
    private PaymentDTO paymentDTO;
    private UUID paymentId;

    @BeforeEach
    void setUp() {
        paymentWebClientGateway = new PaymentWebClientGateway(paymentWebClient, orderMapper);

        paymentId = UUID.randomUUID();

        testOrder = new Order();
        testOrder.setOrderId(UUID.randomUUID());
        testOrder.setCustomerId(UUID.randomUUID());
        testOrder.setCustomerName("John Doe");
        testOrder.setCustomerCpf("12345678901");
        testOrder.setCardNumber("1234-5678-9012-3456");
        testOrder.setPaymentAmount(BigDecimal.valueOf(100.00));

        paymentDTO = new PaymentDTO();
        paymentDTO.setOrderId(testOrder.getOrderId());
        paymentDTO.setCustomerName(testOrder.getCustomerName());
        paymentDTO.setCustomerCpf(testOrder.getCustomerCpf());
        paymentDTO.setCardNumber(testOrder.getCardNumber());
        paymentDTO.setPaymentAmount(testOrder.getPaymentAmount());
    }

    @Test
    void processPayment_ShouldReturnPaymentId() {
        when(orderMapper.mapToDto(testOrder)).thenReturn(paymentDTO);
        when(paymentWebClient.processPayment(paymentDTO)).thenReturn(paymentId);

        Optional<UUID> result = paymentWebClientGateway.processPayment(testOrder);

        assertTrue(result.isPresent());
        assertEquals(paymentId, result.get());
        verify(orderMapper, times(1)).mapToDto(testOrder);
        verify(paymentWebClient, times(1)).processPayment(paymentDTO);
    }

    @Test
    void processPayment_WhenWebClientReturnsNull_ShouldReturnEmptyOptional() {
        when(orderMapper.mapToDto(testOrder)).thenReturn(paymentDTO);
        when(paymentWebClient.processPayment(paymentDTO)).thenReturn(null);

        Optional<UUID> result = paymentWebClientGateway.processPayment(testOrder);

        assertFalse(result.isPresent());
        verify(orderMapper, times(1)).mapToDto(testOrder);
        verify(paymentWebClient, times(1)).processPayment(paymentDTO);
    }

    @Test
    void processPayment_WhenWebClientThrowsException_ShouldPropagateException() {
        RuntimeException webClientException = new RuntimeException("Payment service error");
        when(orderMapper.mapToDto(testOrder)).thenReturn(paymentDTO);
        when(paymentWebClient.processPayment(paymentDTO)).thenThrow(webClientException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentWebClientGateway.processPayment(testOrder));

        assertEquals("Payment service error", exception.getMessage());
        verify(orderMapper, times(1)).mapToDto(testOrder);
        verify(paymentWebClient, times(1)).processPayment(paymentDTO);
    }

    @Test
    void processPayment_WhenMapperThrowsException_ShouldPropagateException() {
        RuntimeException mapperException = new RuntimeException("Mapping failed");
        when(orderMapper.mapToDto(testOrder)).thenThrow(mapperException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentWebClientGateway.processPayment(testOrder));

        assertEquals("Mapping failed", exception.getMessage());
        verify(orderMapper, times(1)).mapToDto(testOrder);
        verify(paymentWebClient, never()).processPayment(any());
    }

    @Test
    void processPayment_WithNullOrder_ShouldHandleGracefully() {
        when(orderMapper.mapToDto(null)).thenReturn(null);
        when(paymentWebClient.processPayment(null)).thenReturn(paymentId);

        Optional<UUID> result = paymentWebClientGateway.processPayment(null);

        assertTrue(result.isPresent());
        assertEquals(paymentId, result.get());
        verify(orderMapper, times(1)).mapToDto(null);
        verify(paymentWebClient, times(1)).processPayment(null);
    }

    @Test
    void processPayment_WithMinimalOrderData_ShouldProcessSuccessfully() {
        Order minimalOrder = new Order();
        minimalOrder.setOrderId(UUID.randomUUID());
        PaymentDTO minimalDTO = new PaymentDTO();
        minimalDTO.setOrderId(minimalOrder.getOrderId());

        when(orderMapper.mapToDto(minimalOrder)).thenReturn(minimalDTO);
        when(paymentWebClient.processPayment(minimalDTO)).thenReturn(paymentId);

        Optional<UUID> result = paymentWebClientGateway.processPayment(minimalOrder);

        assertTrue(result.isPresent());
        assertEquals(paymentId, result.get());
        verify(orderMapper, times(1)).mapToDto(minimalOrder);
        verify(paymentWebClient, times(1)).processPayment(minimalDTO);
    }

    @Test
    void processPayment_WithDifferentPaymentAmounts_ShouldProcessCorrectly() {
        testOrder.setPaymentAmount(BigDecimal.valueOf(250.75));
        paymentDTO.setPaymentAmount(BigDecimal.valueOf(250.75));

        when(orderMapper.mapToDto(testOrder)).thenReturn(paymentDTO);
        when(paymentWebClient.processPayment(paymentDTO)).thenReturn(paymentId);

        Optional<UUID> result = paymentWebClientGateway.processPayment(testOrder);

        assertTrue(result.isPresent());
        assertEquals(paymentId, result.get());
        verify(orderMapper, times(1)).mapToDto(testOrder);
        verify(paymentWebClient, times(1)).processPayment(paymentDTO);
    }

    @Test
    void processPayment_WithCompleteOrderData_ShouldMapAllFields() {
        when(orderMapper.mapToDto(testOrder)).thenReturn(paymentDTO);
        when(paymentWebClient.processPayment(paymentDTO)).thenReturn(paymentId);

        Optional<UUID> result = paymentWebClientGateway.processPayment(testOrder);

        assertTrue(result.isPresent());
        assertEquals(paymentId, result.get());
        verify(orderMapper, times(1)).mapToDto(testOrder);
        verify(paymentWebClient, times(1)).processPayment(paymentDTO);
    }

    @Test
    void processPayment_WhenMapperReturnsNull_ShouldHandleGracefully() {
        when(orderMapper.mapToDto(testOrder)).thenReturn(null);
        when(paymentWebClient.processPayment(null)).thenReturn(paymentId);

        Optional<UUID> result = paymentWebClientGateway.processPayment(testOrder);

        assertTrue(result.isPresent());
        assertEquals(paymentId, result.get());
        verify(orderMapper, times(1)).mapToDto(testOrder);
        verify(paymentWebClient, times(1)).processPayment(null);
    }
}

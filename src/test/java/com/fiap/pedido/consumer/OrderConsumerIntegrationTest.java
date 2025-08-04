package com.fiap.pedido.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.OrderStatus;
import com.fiap.pedido.domain.PaymentStatus;
import com.fiap.pedido.usecase.ProcessOrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderConsumerIntegrationTest {

    @MockBean
    private ProcessOrderUseCase processOrderUseCase;

    private OrderConsumer orderConsumer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        orderConsumer = new OrderConsumer(objectMapper, processOrderUseCase);
    }

    @Test
    void consume_WithValidOrderMessage_ShouldProcessOrder() throws Exception {
        Order testOrder = createTestOrder();
        String orderJson = objectMapper.writeValueAsString(testOrder);

        orderConsumer.receiveOrder(orderJson);

        verify(processOrderUseCase, times(1)).execute(any(Order.class));
    }

    @Test
    void consume_WithInvalidJson_ShouldHandleGracefully() {
        String invalidJson = "{ invalid json }";

        orderConsumer.receiveOrder(invalidJson);

        verify(processOrderUseCase, never()).execute(any(Order.class));
    }

    @Test
    void consume_WithEmptyMessage_ShouldHandleGracefully() {
        orderConsumer.receiveOrder("");

        verify(processOrderUseCase, never()).execute(any(Order.class));
    }

    @Test
    void consume_WithNullMessage_ShouldHandleGracefully() {
        orderConsumer.receiveOrder(null);

        verify(processOrderUseCase, never()).execute(any(Order.class));
    }

    @Test
    void consume_WhenProcessOrderThrowsException_ShouldHandleGracefully() throws Exception {
        Order testOrder = createTestOrder();
        String orderJson = objectMapper.writeValueAsString(testOrder);

        doThrow(new RuntimeException("Processing error")).when(processOrderUseCase).execute(any(Order.class));

        orderConsumer.receiveOrder(orderJson);

        verify(processOrderUseCase, times(1)).execute(any(Order.class));
    }

    private Order createTestOrder() {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomerId(UUID.randomUUID());
        order.setCustomerName("Test Customer");
        order.setCustomerCpf("12345678901");
        order.setCardNumber("**** **** **** 1234");
        order.setStatus(OrderStatus.ABERTO);
        order.setPaymentId(UUID.randomUUID());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentAmount(BigDecimal.valueOf(100.00));
        return order;
    }
}

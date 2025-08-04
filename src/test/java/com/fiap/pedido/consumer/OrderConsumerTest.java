package com.fiap.pedido.consumer;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.usecase.ProcessOrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProcessOrderUseCase processOrderUseCase;

    private OrderConsumer orderConsumer;

    private Order testOrder;
    private String validOrderMessage;

    @BeforeEach
    void setUp() {
        orderConsumer = new OrderConsumer(objectMapper, processOrderUseCase);

        Item item = new Item();
        item.setSku("SKU001");
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(50.00));

        testOrder = new Order();
        testOrder.setOrderId(UUID.randomUUID());
        testOrder.setCustomerId(UUID.randomUUID());
        testOrder.setCustomerName("John Doe");
        testOrder.setCardNumber("1234-5678-9012-3456");
        testOrder.setItems(Collections.singletonList(item));
        testOrder.setPaymentAmount(BigDecimal.valueOf(100.00));

        validOrderMessage = "{\"orderId\":\"" + testOrder.getOrderId() + "\",\"customerId\":\"" + testOrder.getCustomerId() + "\",\"customerName\":\"John Doe\"}";
    }

    @Test
    void receiveOrder_ShouldProcessOrderSuccessfully() throws Exception {
        when(objectMapper.readValue(validOrderMessage, Order.class)).thenReturn(testOrder);
        doNothing().when(processOrderUseCase).execute(testOrder);

        assertDoesNotThrow(() -> orderConsumer.receiveOrder(validOrderMessage));

        verify(objectMapper, times(1)).readValue(validOrderMessage, Order.class);
        verify(processOrderUseCase, times(1)).execute(testOrder);
    }

    @Test
    void receiveOrder_WhenDeserializationFails_ShouldLogErrorAndNotCrash() throws Exception {
        RuntimeException deserializationException = new RuntimeException("Failed to parse order message");
        when(objectMapper.readValue(validOrderMessage, Order.class)).thenThrow(deserializationException);

        assertDoesNotThrow(() -> orderConsumer.receiveOrder(validOrderMessage));

        verify(objectMapper, times(1)).readValue(validOrderMessage, Order.class);
        verify(processOrderUseCase, never()).execute(any(Order.class));
    }

    @Test
    void receiveOrder_WhenProcessOrderUseCaseThrowsException_ShouldLogErrorAndNotCrash() throws Exception {
        RuntimeException processException = new RuntimeException("Processing failed");
        when(objectMapper.readValue(validOrderMessage, Order.class)).thenReturn(testOrder);
        doThrow(processException).when(processOrderUseCase).execute(testOrder);

        assertDoesNotThrow(() -> orderConsumer.receiveOrder(validOrderMessage));

        verify(objectMapper, times(1)).readValue(validOrderMessage, Order.class);
        verify(processOrderUseCase, times(1)).execute(testOrder);
    }

    @Test
    void receiveOrder_WithEmptyMessage_ShouldHandleGracefully() throws Exception {
        String emptyMessage = "";
        RuntimeException deserializationException = new RuntimeException("Failed to parse order message");
        when(objectMapper.readValue(emptyMessage, Order.class)).thenThrow(deserializationException);

        assertDoesNotThrow(() -> orderConsumer.receiveOrder(emptyMessage));

        verify(objectMapper, times(1)).readValue(emptyMessage, Order.class);
        verify(processOrderUseCase, never()).execute(any(Order.class));
    }

    @Test
    void receiveOrder_WithInvalidJsonStructure_ShouldHandleGracefully() throws Exception {
        String invalidJson = "{invalid:json}";
        JsonMappingException deserializationException = new JsonMappingException("Malformed JSON");
        when(objectMapper.readValue(invalidJson, Order.class)).thenThrow(deserializationException);

        assertDoesNotThrow(() -> orderConsumer.receiveOrder(invalidJson));

        verify(objectMapper, times(1)).readValue(invalidJson, Order.class);
        verify(processOrderUseCase, never()).execute(any(Order.class));
    }

    @Test
    void receiveOrder_WithPartialOrderData_ShouldStillProcessOrder() throws Exception {
        Order partialOrder = new Order();
        partialOrder.setOrderId(UUID.randomUUID());
        partialOrder.setCustomerId(UUID.randomUUID());

        String partialMessage = "{\"orderId\":\"" + partialOrder.getOrderId() + "\"}";
        when(objectMapper.readValue(partialMessage, Order.class)).thenReturn(partialOrder);
        doNothing().when(processOrderUseCase).execute(partialOrder);

        assertDoesNotThrow(() -> orderConsumer.receiveOrder(partialMessage));

        verify(objectMapper, times(1)).readValue(partialMessage, Order.class);
        verify(processOrderUseCase, times(1)).execute(partialOrder);
    }

    @Test
    void receiveOrder_WithComplexOrderData_ShouldProcessSuccessfully() throws Exception {
        String complexMessage = "{\"orderId\":\"" + testOrder.getOrderId() + "\",\"customerId\":\"" + testOrder.getCustomerId() + "\",\"items\":[{\"sku\":\"SKU001\",\"quantity\":2}]}";
        when(objectMapper.readValue(complexMessage, Order.class)).thenReturn(testOrder);
        doNothing().when(processOrderUseCase).execute(testOrder);

        assertDoesNotThrow(() -> orderConsumer.receiveOrder(complexMessage));

        verify(objectMapper, times(1)).readValue(complexMessage, Order.class);
        verify(processOrderUseCase, times(1)).execute(testOrder);
    }
}

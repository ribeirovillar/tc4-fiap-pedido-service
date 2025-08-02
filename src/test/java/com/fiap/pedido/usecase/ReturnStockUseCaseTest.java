package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.gateway.StockGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReturnStockUseCaseTest {

    @Mock
    private StockGateway stockGateway;

    private ReturnStockUseCase returnStockUseCase;

    private Order testOrder;
    private List<Item> testItems;

    @BeforeEach
    void setUp() {
        returnStockUseCase = new ReturnStockUseCase(stockGateway);

        Item item1 = new Item();
        item1.setSku("SKU001");
        item1.setQuantity(2);
        item1.setPrice(BigDecimal.valueOf(50.00));

        Item item2 = new Item();
        item2.setSku("SKU002");
        item2.setQuantity(1);
        item2.setPrice(BigDecimal.valueOf(30.00));

        testItems = Arrays.asList(item1, item2);

        testOrder = new Order();
        testOrder.setOrderId(UUID.randomUUID());
        testOrder.setItems(testItems);
    }

    @Test
    void execute_ShouldReturnStockSuccessfully() {
        doNothing().when(stockGateway).returnStock(testItems);

        assertDoesNotThrow(() -> returnStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).returnStock(testItems);
    }

    @Test
    void execute_WithEmptyItemsList_ShouldCallGatewayWithEmptyList() {
        testOrder.setItems(Collections.emptyList());
        doNothing().when(stockGateway).returnStock(Collections.emptyList());

        assertDoesNotThrow(() -> returnStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).returnStock(Collections.emptyList());
    }

    @Test
    void execute_WhenStockGatewayThrowsException_ShouldNotPropagateException() {
        RuntimeException gatewayException = new RuntimeException("Stock service unavailable");
        doThrow(gatewayException).when(stockGateway).returnStock(testItems);

        assertDoesNotThrow(() -> returnStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).returnStock(testItems);
    }

    @Test
    void execute_WithSingleItem_ShouldReturnSuccessfully() {
        Item singleItem = testItems.getFirst();
        testOrder.setItems(Collections.singletonList(singleItem));
        List<Item> singleItemList = Collections.singletonList(singleItem);

        doNothing().when(stockGateway).returnStock(singleItemList);

        assertDoesNotThrow(() -> returnStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).returnStock(singleItemList);
    }

    @Test
    void execute_WithNullItems_ShouldCallGatewayWithNull() {
        testOrder.setItems(null);
        doNothing().when(stockGateway).returnStock(null);

        assertDoesNotThrow(() -> returnStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).returnStock(null);
    }

    @Test
    void execute_WhenMultipleExceptionsOccur_ShouldHandleGracefully() {
        RuntimeException firstException = new RuntimeException("First error");
        doThrow(firstException).when(stockGateway).returnStock(testItems);

        assertDoesNotThrow(() -> returnStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).returnStock(testItems);
    }

    @Test
    void execute_ShouldAlwaysCallStockGateway() {
        returnStockUseCase.execute(testOrder);

        verify(stockGateway, times(1)).returnStock(testItems);
    }

    @Test
    void execute_WhenRuntimeExceptionOccurs_ShouldContinueExecution() {
        RuntimeException runtimeException = new RuntimeException("Database connection error");
        doThrow(runtimeException).when(stockGateway).returnStock(testItems);

        assertDoesNotThrow(() -> returnStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).returnStock(testItems);
    }
}

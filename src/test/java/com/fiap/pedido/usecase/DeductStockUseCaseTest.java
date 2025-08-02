package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.exception.InsufficientStockException;
import com.fiap.pedido.gateway.StockGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeductStockUseCaseTest {

    @Mock
    private StockGateway stockGateway;
    @Mock
    private HttpClientErrorException.BadRequest badRequestException;

    private DeductStockUseCase deductStockUseCase;

    private Order testOrder;
    private List<Item> testItems;

    @BeforeEach
    void setUp() {
        deductStockUseCase = new DeductStockUseCase(stockGateway);

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
    void execute_ShouldDeductStockSuccessfully() {
        doNothing().when(stockGateway).deductStock(testItems);

        assertDoesNotThrow(() -> deductStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).deductStock(testItems);
    }

    @Test
    void execute_WithEmptyItemsList_ShouldCallGatewayWithEmptyList() {
        testOrder.setItems(Collections.emptyList());
        doNothing().when(stockGateway).deductStock(Collections.emptyList());

        assertDoesNotThrow(() -> deductStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).deductStock(Collections.emptyList());
    }

    @Test
    void execute_WhenHttpClientErrorBadRequest_ShouldThrowInsufficientStockException() {
        doThrow(badRequestException).when(stockGateway).deductStock(testItems);

        assertThrows(InsufficientStockException.class,
                () -> deductStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).deductStock(testItems);
    }

    @Test
    void execute_WhenGenericExceptionOccurs_ShouldPropagateException() {
        RuntimeException genericException = new RuntimeException("Stock service unavailable");
        doThrow(genericException).when(stockGateway).deductStock(testItems);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deductStockUseCase.execute(testOrder));

        assertEquals("Stock service unavailable", exception.getMessage());
        verify(stockGateway, times(1)).deductStock(testItems);
    }

    @Test
    void execute_WithSingleItem_ShouldDeductSuccessfully() {
        Item singleItem = testItems.getFirst();
        testOrder.setItems(Collections.singletonList(singleItem));
        List<Item> singleItemList = Collections.singletonList(singleItem);

        doNothing().when(stockGateway).deductStock(singleItemList);

        assertDoesNotThrow(() -> deductStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).deductStock(singleItemList);
    }

    @Test
    void execute_WithNullItems_ShouldCallGatewayWithNull() {
        testOrder.setItems(null);
        doNothing().when(stockGateway).deductStock(null);

        assertDoesNotThrow(() -> deductStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).deductStock(null);
    }

    @Test
    void execute_WhenStockGatewayThrowsHttpClientError_ShouldWrapInInsufficientStockException() {

        doThrow(badRequestException).when(stockGateway).deductStock(testItems);

        assertThrows(InsufficientStockException.class,
                () -> deductStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).deductStock(testItems);
    }

    @Test
    void execute_WhenMultipleItemsAndOneHasInsufficientStock_ShouldThrowException() {

        doThrow(badRequestException).when(stockGateway).deductStock(testItems);

        assertThrows(InsufficientStockException.class,
                () -> deductStockUseCase.execute(testOrder));

        verify(stockGateway, times(1)).deductStock(testItems);
    }
}

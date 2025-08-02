package com.fiap.pedido.gateway.web;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.gateway.web.client.StockWebClient;
import com.fiap.pedido.gateway.web.json.StockDTO;
import com.fiap.pedido.mapper.OrderItemMapper;
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
class StockWebClientGatewayTest {

    @Mock
    private StockWebClient stockWebClient;

    @Mock
    private OrderItemMapper orderItemMapper;

    private StockWebClientGateway stockWebClientGateway;

    private List<Item> testItems;
    private List<StockDTO> testStockDTOs;

    @BeforeEach
    void setUp() {
        stockWebClientGateway = new StockWebClientGateway(stockWebClient, orderItemMapper);

        Item item1 = new Item();
        item1.setId(UUID.randomUUID());
        item1.setSku("SKU001");
        item1.setQuantity(2);
        item1.setPrice(BigDecimal.valueOf(50.00));

        Item item2 = new Item();
        item2.setId(UUID.randomUUID());
        item2.setSku("SKU002");
        item2.setQuantity(1);
        item2.setPrice(BigDecimal.valueOf(30.00));

        testItems = Arrays.asList(item1, item2);

        StockDTO stockDTO1 = new StockDTO();
        stockDTO1.setProductId(item1.getId());
        stockDTO1.setQuantity(2);

        StockDTO stockDTO2 = new StockDTO();
        stockDTO2.setProductId(item2.getId());
        stockDTO2.setQuantity(1);

        testStockDTOs = Arrays.asList(stockDTO1, stockDTO2);
    }

    @Test
    void deductStock_ShouldMapItemsAndCallWebClient() {
        when(orderItemMapper.map(testItems)).thenReturn(testStockDTOs);
        doNothing().when(stockWebClient).deductStock(testStockDTOs);

        assertDoesNotThrow(() -> stockWebClientGateway.deductStock(testItems));

        verify(orderItemMapper, times(1)).map(testItems);
        verify(stockWebClient, times(1)).deductStock(testStockDTOs);
    }

    @Test
    void deductStock_WithEmptyItems_ShouldCallWebClientWithEmptyList() {
        List<Item> emptyItems = Collections.emptyList();
        List<StockDTO> emptyStockDTOs = Collections.emptyList();
        when(orderItemMapper.map(emptyItems)).thenReturn(emptyStockDTOs);
        doNothing().when(stockWebClient).deductStock(emptyStockDTOs);

        assertDoesNotThrow(() -> stockWebClientGateway.deductStock(emptyItems));

        verify(orderItemMapper, times(1)).map(emptyItems);
        verify(stockWebClient, times(1)).deductStock(emptyStockDTOs);
    }

    @Test
    void deductStock_WhenWebClientThrowsException_ShouldPropagateException() {
        RuntimeException webClientException = new RuntimeException("Stock service error");
        when(orderItemMapper.map(testItems)).thenReturn(testStockDTOs);
        doThrow(webClientException).when(stockWebClient).deductStock(testStockDTOs);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> stockWebClientGateway.deductStock(testItems));

        assertEquals("Stock service error", exception.getMessage());
        verify(orderItemMapper, times(1)).map(testItems);
        verify(stockWebClient, times(1)).deductStock(testStockDTOs);
    }

    @Test
    void deductStock_WhenMapperThrowsException_ShouldPropagateException() {
        RuntimeException mapperException = new RuntimeException("Mapping failed");
        when(orderItemMapper.map(testItems)).thenThrow(mapperException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> stockWebClientGateway.deductStock(testItems));

        assertEquals("Mapping failed", exception.getMessage());
        verify(orderItemMapper, times(1)).map(testItems);
        verify(stockWebClient, never()).deductStock(any());
    }

    @Test
    void returnStock_ShouldMapItemsAndCallWebClient() {
        when(orderItemMapper.map(testItems)).thenReturn(testStockDTOs);
        doNothing().when(stockWebClient).returnStock(testStockDTOs);

        assertDoesNotThrow(() -> stockWebClientGateway.returnStock(testItems));

        verify(orderItemMapper, times(1)).map(testItems);
        verify(stockWebClient, times(1)).returnStock(testStockDTOs);
    }

    @Test
    void returnStock_WithEmptyItems_ShouldCallWebClientWithEmptyList() {
        List<Item> emptyItems = Collections.emptyList();
        List<StockDTO> emptyStockDTOs = Collections.emptyList();
        when(orderItemMapper.map(emptyItems)).thenReturn(emptyStockDTOs);
        doNothing().when(stockWebClient).returnStock(emptyStockDTOs);

        assertDoesNotThrow(() -> stockWebClientGateway.returnStock(emptyItems));

        verify(orderItemMapper, times(1)).map(emptyItems);
        verify(stockWebClient, times(1)).returnStock(emptyStockDTOs);
    }

    @Test
    void returnStock_WhenWebClientThrowsException_ShouldPropagateException() {
        RuntimeException webClientException = new RuntimeException("Return stock service error");
        when(orderItemMapper.map(testItems)).thenReturn(testStockDTOs);
        doThrow(webClientException).when(stockWebClient).returnStock(testStockDTOs);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> stockWebClientGateway.returnStock(testItems));

        assertEquals("Return stock service error", exception.getMessage());
        verify(orderItemMapper, times(1)).map(testItems);
        verify(stockWebClient, times(1)).returnStock(testStockDTOs);
    }

    @Test
    void deductStock_WithSingleItem_ShouldProcessSuccessfully() {
        List<Item> singleItem = Collections.singletonList(testItems.getFirst());
        List<StockDTO> singleStockDTO = Collections.singletonList(testStockDTOs.getFirst());
        when(orderItemMapper.map(singleItem)).thenReturn(singleStockDTO);
        doNothing().when(stockWebClient).deductStock(singleStockDTO);

        assertDoesNotThrow(() -> stockWebClientGateway.deductStock(singleItem));

        verify(orderItemMapper, times(1)).map(singleItem);
        verify(stockWebClient, times(1)).deductStock(singleStockDTO);
    }

    @Test
    void returnStock_WithSingleItem_ShouldProcessSuccessfully() {
        List<Item> singleItem = Collections.singletonList(testItems.getFirst());
        List<StockDTO> singleStockDTO = Collections.singletonList(testStockDTOs.getFirst());
        when(orderItemMapper.map(singleItem)).thenReturn(singleStockDTO);
        doNothing().when(stockWebClient).returnStock(singleStockDTO);

        assertDoesNotThrow(() -> stockWebClientGateway.returnStock(singleItem));

        verify(orderItemMapper, times(1)).map(singleItem);
        verify(stockWebClient, times(1)).returnStock(singleStockDTO);
    }
}

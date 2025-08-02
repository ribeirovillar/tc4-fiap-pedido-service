package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.OrderStatus;
import com.fiap.pedido.exception.DataEnrichmentException;
import com.fiap.pedido.gateway.OrderGateway;
import com.fiap.pedido.usecase.load.EnrichOrderDataStrategy;
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
class EnrichOrderDetailsUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private EnrichOrderDataStrategy strategy1;

    @Mock
    private EnrichOrderDataStrategy strategy2;

    private EnrichOrderDetailsUseCase enrichOrderDetailsUseCase;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        List<EnrichOrderDataStrategy> strategies = Arrays.asList(strategy1, strategy2);
        enrichOrderDetailsUseCase = new EnrichOrderDetailsUseCase(orderGateway, strategies);

        testOrder = new Order();
        testOrder.setOrderId(UUID.randomUUID());
        testOrder.setCustomerId(UUID.randomUUID());
        testOrder.setCustomerName("John Doe");
        testOrder.setStatus(OrderStatus.ABERTO);
        testOrder.setPaymentAmount(BigDecimal.valueOf(100.00));
    }

    @Test
    void execute_ShouldEnrichOrderAndSaveSuccessfully() {
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        assertDoesNotThrow(() -> enrichOrderDetailsUseCase.execute(testOrder));

        verify(strategy1, times(1)).enrich(testOrder);
        verify(strategy2, times(1)).enrich(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_ShouldExecuteStrategiesInOrder() {
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        enrichOrderDetailsUseCase.execute(testOrder);

        var inOrder = inOrder(strategy1, strategy2, orderGateway);
        inOrder.verify(strategy1).enrich(testOrder);
        inOrder.verify(strategy2).enrich(testOrder);
        inOrder.verify(orderGateway).save(testOrder);
    }

    @Test
    void execute_WithEmptyStrategiesList_ShouldOnlySaveOrder() {
        List<EnrichOrderDataStrategy> emptyStrategies = Collections.emptyList();
        enrichOrderDetailsUseCase = new EnrichOrderDetailsUseCase(orderGateway, emptyStrategies);
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        assertDoesNotThrow(() -> enrichOrderDetailsUseCase.execute(testOrder));

        verify(orderGateway, times(1)).save(testOrder);
        verifyNoInteractions(strategy1, strategy2);
    }

    @Test
    void execute_WhenStrategyThrowsException_ShouldThrowDataEnrichmentException() {
        RuntimeException strategyException = new RuntimeException("Strategy failed");
        doThrow(strategyException).when(strategy1).enrich(testOrder);

        DataEnrichmentException exception = assertThrows(DataEnrichmentException.class,
                () -> enrichOrderDetailsUseCase.execute(testOrder));

        assertEquals("Failed to enrich order details", exception.getMessage());
        assertEquals(strategyException, exception.getCause());
        verify(strategy1, times(1)).enrich(testOrder);
        verify(strategy2, never()).enrich(testOrder);
        verify(orderGateway, never()).save(testOrder);
    }

    @Test
    void execute_WhenSecondStrategyThrowsException_ShouldThrowDataEnrichmentException() {
        RuntimeException strategyException = new RuntimeException("Second strategy failed");
        doNothing().when(strategy1).enrich(testOrder);
        doThrow(strategyException).when(strategy2).enrich(testOrder);

        DataEnrichmentException exception = assertThrows(DataEnrichmentException.class,
                () -> enrichOrderDetailsUseCase.execute(testOrder));

        assertEquals("Failed to enrich order details", exception.getMessage());
        assertEquals(strategyException, exception.getCause());
        verify(strategy1, times(1)).enrich(testOrder);
        verify(strategy2, times(1)).enrich(testOrder);
        verify(orderGateway, never()).save(testOrder);
    }

    @Test
    void execute_WhenGatewaySaveReturnsEmpty_ShouldThrowDataEnrichmentException() {
        when(orderGateway.save(testOrder)).thenReturn(Optional.empty());

        DataEnrichmentException exception = assertThrows(DataEnrichmentException.class,
                () -> enrichOrderDetailsUseCase.execute(testOrder));

        assertEquals("Failed to enrich order details", exception.getMessage());
        verify(strategy1, times(1)).enrich(testOrder);
        verify(strategy2, times(1)).enrich(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_WhenGatewayThrowsException_ShouldThrowDataEnrichmentException() {
        RuntimeException gatewayException = new RuntimeException("Database error");
        when(orderGateway.save(testOrder)).thenThrow(gatewayException);

        DataEnrichmentException exception = assertThrows(DataEnrichmentException.class,
                () -> enrichOrderDetailsUseCase.execute(testOrder));

        assertEquals("Failed to enrich order details", exception.getMessage());
        assertEquals(gatewayException, exception.getCause());
        verify(strategy1, times(1)).enrich(testOrder);
        verify(strategy2, times(1)).enrich(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_WhenStrategyModifiesOrder_ShouldSaveModifiedOrder() {
        doAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setCustomerName("Modified Name");
            return null;
        }).when(strategy1).enrich(testOrder);

        when(orderGateway.save(any(Order.class))).thenReturn(Optional.of(testOrder));

        enrichOrderDetailsUseCase.execute(testOrder);

        assertEquals("Modified Name", testOrder.getCustomerName());
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void execute_WithSingleStrategy_ShouldExecuteSuccessfully() {
        List<EnrichOrderDataStrategy> singleStrategy = Collections.singletonList(strategy1);
        enrichOrderDetailsUseCase = new EnrichOrderDetailsUseCase(orderGateway, singleStrategy);
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));

        assertDoesNotThrow(() -> enrichOrderDetailsUseCase.execute(testOrder));

        verify(strategy1, times(1)).enrich(testOrder);
        verify(strategy2, never()).enrich(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }

    @Test
    void constructor_ShouldInitializeFieldsCorrectly() {
        List<EnrichOrderDataStrategy> strategies = Arrays.asList(strategy1, strategy2);

        EnrichOrderDetailsUseCase useCase = new EnrichOrderDetailsUseCase(orderGateway, strategies);

        assertNotNull(useCase);
        when(orderGateway.save(testOrder)).thenReturn(Optional.of(testOrder));
        useCase.execute(testOrder);

        verify(strategy1, times(1)).enrich(testOrder);
        verify(strategy2, times(1)).enrich(testOrder);
        verify(orderGateway, times(1)).save(testOrder);
    }
}

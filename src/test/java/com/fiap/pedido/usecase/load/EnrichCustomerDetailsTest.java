package com.fiap.pedido.usecase.load;

import com.fiap.pedido.domain.Customer;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.usecase.RetrieveCustomerByIdUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrichCustomerDetailsTest {

    @Mock
    private RetrieveCustomerByIdUseCase retrieveCustomerByIdUseCase;

    private EnrichCustomerDetails enrichCustomerDetails;

    private Order testOrder;
    private Customer testCustomer;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        enrichCustomerDetails = new EnrichCustomerDetails(retrieveCustomerByIdUseCase);

        customerId = UUID.randomUUID();
        testCustomer = new Customer();
        testCustomer.setId(customerId);
        testCustomer.setFullName("John Doe");
        testCustomer.setCpf("12345678901");

        testOrder = new Order();
        testOrder.setOrderId(UUID.randomUUID());
        testOrder.setCustomerId(customerId);
    }

    @Test
    void enrich_ShouldSetCustomerNameAndCpf() {
        when(retrieveCustomerByIdUseCase.execute(customerId)).thenReturn(testCustomer);

        enrichCustomerDetails.enrich(testOrder);

        assertEquals("John Doe", testOrder.getCustomerName());
        assertEquals("12345678901", testOrder.getCustomerCpf());
        verify(retrieveCustomerByIdUseCase, times(1)).execute(customerId);
    }

    @Test
    void enrich_WithNullCustomerName_ShouldSetNullName() {
        testCustomer.setFullName(null);
        when(retrieveCustomerByIdUseCase.execute(customerId)).thenReturn(testCustomer);

        enrichCustomerDetails.enrich(testOrder);

        assertNull(testOrder.getCustomerName());
        assertEquals("12345678901", testOrder.getCustomerCpf());
        verify(retrieveCustomerByIdUseCase, times(1)).execute(customerId);
    }

    @Test
    void enrich_WithNullCustomerCpf_ShouldSetNullCpf() {
        testCustomer.setCpf(null);
        when(retrieveCustomerByIdUseCase.execute(customerId)).thenReturn(testCustomer);

        enrichCustomerDetails.enrich(testOrder);

        assertEquals("John Doe", testOrder.getCustomerName());
        assertNull(testOrder.getCustomerCpf());
        verify(retrieveCustomerByIdUseCase, times(1)).execute(customerId);
    }

    @Test
    void enrich_WithEmptyCustomerData_ShouldSetEmptyValues() {
        testCustomer.setFullName("");
        testCustomer.setCpf("");
        when(retrieveCustomerByIdUseCase.execute(customerId)).thenReturn(testCustomer);

        enrichCustomerDetails.enrich(testOrder);

        assertEquals("", testOrder.getCustomerName());
        assertEquals("", testOrder.getCustomerCpf());
        verify(retrieveCustomerByIdUseCase, times(1)).execute(customerId);
    }

    @Test
    void enrich_WhenUseCaseThrowsException_ShouldPropagateException() {
        RuntimeException customerException = new RuntimeException("Customer not found");
        when(retrieveCustomerByIdUseCase.execute(customerId)).thenThrow(customerException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> enrichCustomerDetails.enrich(testOrder));

        assertEquals("Customer not found", exception.getMessage());
        verify(retrieveCustomerByIdUseCase, times(1)).execute(customerId);
    }

    @Test
    void enrich_WithOrderAlreadyHavingCustomerData_ShouldOverrideData() {
        testOrder.setCustomerName("Old Name");
        testOrder.setCustomerCpf("98765432109");
        when(retrieveCustomerByIdUseCase.execute(customerId)).thenReturn(testCustomer);

        enrichCustomerDetails.enrich(testOrder);

        assertEquals("John Doe", testOrder.getCustomerName());
        assertEquals("12345678901", testOrder.getCustomerCpf());
        verify(retrieveCustomerByIdUseCase, times(1)).execute(customerId);
    }

    @Test
    void enrich_WithDifferentCustomerId_ShouldCallCorrectCustomer() {
        UUID differentCustomerId = UUID.randomUUID();
        testOrder.setCustomerId(differentCustomerId);
        Customer differentCustomer = new Customer();
        differentCustomer.setId(differentCustomerId);
        differentCustomer.setFullName("Jane Smith");
        differentCustomer.setCpf("11122233344");

        when(retrieveCustomerByIdUseCase.execute(differentCustomerId)).thenReturn(differentCustomer);

        enrichCustomerDetails.enrich(testOrder);

        assertEquals("Jane Smith", testOrder.getCustomerName());
        assertEquals("11122233344", testOrder.getCustomerCpf());
        verify(retrieveCustomerByIdUseCase, times(1)).execute(differentCustomerId);
        verify(retrieveCustomerByIdUseCase, never()).execute(customerId);
    }

    @Test
    void enrich_WithNullCustomerId_ShouldCallUseCaseWithNull() {
        testOrder.setCustomerId(null);
        when(retrieveCustomerByIdUseCase.execute(null)).thenReturn(testCustomer);

        enrichCustomerDetails.enrich(testOrder);

        assertEquals("John Doe", testOrder.getCustomerName());
        assertEquals("12345678901", testOrder.getCustomerCpf());
        verify(retrieveCustomerByIdUseCase, times(1)).execute(null);
    }
}

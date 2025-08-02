package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Customer;
import com.fiap.pedido.exception.CustomerException;
import com.fiap.pedido.gateway.CustomerGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetrieveCustomerByIdUseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    private RetrieveCustomerByIdUseCase retrieveCustomerByIdUseCase;

    private Customer testCustomer;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        retrieveCustomerByIdUseCase = new RetrieveCustomerByIdUseCase(customerGateway);

        customerId = UUID.randomUUID();
        testCustomer = new Customer();
        testCustomer.setId(customerId);
        testCustomer.setFullName("John Doe");
        testCustomer.setCpf("12345678901");
    }

    @Test
    void execute_ShouldReturnCustomerWhenFound() {
        when(customerGateway.findById(customerId)).thenReturn(Optional.of(testCustomer));

        Customer result = retrieveCustomerByIdUseCase.execute(customerId);

        assertNotNull(result);
        assertEquals(customerId, result.getId());
        assertEquals("John Doe", result.getFullName());
        assertEquals("12345678901", result.getCpf());
        verify(customerGateway, times(1)).findById(customerId);
    }

    @Test
    void execute_WhenCustomerNotFound_ShouldThrowCustomerException() {
        when(customerGateway.findById(customerId)).thenReturn(Optional.empty());

        CustomerException exception = assertThrows(CustomerException.class,
                () -> retrieveCustomerByIdUseCase.execute(customerId));

        assertEquals("Failed to retrieve customer with ID: " + customerId, exception.getMessage());
        verify(customerGateway, times(1)).findById(customerId);
    }

    @Test
    void execute_WithNullCustomerId_ShouldCallGatewayWithNull() {
        when(customerGateway.findById(null)).thenReturn(Optional.empty());

        CustomerException exception = assertThrows(CustomerException.class,
                () -> retrieveCustomerByIdUseCase.execute(null));

        assertEquals("Failed to retrieve customer with ID: null", exception.getMessage());
        verify(customerGateway, times(1)).findById(null);
    }

    @Test
    void execute_WhenGatewayThrowsException_ShouldPropagateException() {
        RuntimeException gatewayException = new RuntimeException("Database connection error");
        when(customerGateway.findById(customerId)).thenThrow(gatewayException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> retrieveCustomerByIdUseCase.execute(customerId));

        assertEquals("Database connection error", exception.getMessage());
        verify(customerGateway, times(1)).findById(customerId);
    }

    @Test
    void execute_WithDifferentCustomerIds_ShouldReturnCorrectCustomer() {
        UUID anotherId = UUID.randomUUID();
        Customer anotherCustomer = new Customer();
        anotherCustomer.setId(anotherId);
        anotherCustomer.setFullName("Jane Smith");

        when(customerGateway.findById(customerId)).thenReturn(Optional.of(testCustomer));
        when(customerGateway.findById(anotherId)).thenReturn(Optional.of(anotherCustomer));

        Customer result1 = retrieveCustomerByIdUseCase.execute(customerId);
        Customer result2 = retrieveCustomerByIdUseCase.execute(anotherId);

        assertEquals("John Doe", result1.getFullName());
        assertEquals("Jane Smith", result2.getFullName());
        verify(customerGateway, times(1)).findById(customerId);
        verify(customerGateway, times(1)).findById(anotherId);
    }

    @Test
    void execute_WhenCustomerHasMinimalData_ShouldReturnCustomer() {
        Customer minimalCustomer = new Customer();
        minimalCustomer.setId(customerId);

        when(customerGateway.findById(customerId)).thenReturn(Optional.of(minimalCustomer));

        Customer result = retrieveCustomerByIdUseCase.execute(customerId);

        assertNotNull(result);
        assertEquals(customerId, result.getId());
        assertNull(result.getFullName());
        assertNull(result.getCpf());
        verify(customerGateway, times(1)).findById(customerId);
    }
}

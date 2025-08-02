package com.fiap.pedido.gateway.web;

import com.fiap.pedido.domain.Customer;
import com.fiap.pedido.gateway.web.client.CustomerWebClient;
import com.fiap.pedido.gateway.web.json.AddressDTO;
import com.fiap.pedido.gateway.web.json.CustomerResponseDTO;
import com.fiap.pedido.mapper.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerWebClientGatewayTest {

    @Mock
    private CustomerWebClient customerWebClient;

    @Mock
    private CustomerMapper customerMapper;

    private CustomerWebClientGateway customerWebClientGateway;

    private UUID customerId;
    private CustomerResponseDTO customerResponseDTO;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customerWebClientGateway = new CustomerWebClientGateway(customerWebClient, customerMapper);

        customerId = UUID.randomUUID();

        AddressDTO address = AddressDTO.builder()
                .street("Main Street")
                .number("123")
                .city("São Paulo")
                .state("SP")
                .zipCode("01234-567")
                .build();

        customerResponseDTO = CustomerResponseDTO.builder()
                .id(customerId)
                .fullName("John Doe")
                .cpf("12345678901")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .build();

        customer = new Customer();
        customer.setId(customerId);
        customer.setFullName("John Doe");
        customer.setCpf("12345678901");
    }

    @Test
    void findById_ShouldReturnMappedCustomer() {
        when(customerWebClient.findCustomerById(customerId)).thenReturn(customerResponseDTO);
        when(customerMapper.map(customerResponseDTO)).thenReturn(customer);

        Optional<Customer> result = customerWebClientGateway.findById(customerId);

        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        assertEquals(customerId, result.get().getId());
        assertEquals("John Doe", result.get().getFullName());
        assertEquals("12345678901", result.get().getCpf());
        verify(customerWebClient, times(1)).findCustomerById(customerId);
        verify(customerMapper, times(1)).map(customerResponseDTO);
    }

    @Test
    void findById_WhenWebClientReturnsNull_ShouldReturnEmptyOptional() {
        when(customerWebClient.findCustomerById(customerId)).thenReturn(null);

        Optional<Customer> result = customerWebClientGateway.findById(customerId);

        assertFalse(result.isPresent());
        verify(customerWebClient, times(1)).findCustomerById(customerId);
        verify(customerMapper, never()).map(any());
    }

    @Test
    void findById_WhenMapperReturnsNull_ShouldReturnEmptyOptional() {
        when(customerWebClient.findCustomerById(customerId)).thenReturn(customerResponseDTO);
        when(customerMapper.map(customerResponseDTO)).thenReturn(null);

        Optional<Customer> result = customerWebClientGateway.findById(customerId);

        assertFalse(result.isPresent());
        verify(customerWebClient, times(1)).findCustomerById(customerId);
        verify(customerMapper, times(1)).map(customerResponseDTO);
    }

    @Test
    void findById_WhenWebClientThrowsException_ShouldPropagateException() {
        RuntimeException webClientException = new RuntimeException("Customer service error");
        when(customerWebClient.findCustomerById(customerId)).thenThrow(webClientException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerWebClientGateway.findById(customerId));

        assertEquals("Customer service error", exception.getMessage());
        verify(customerWebClient, times(1)).findCustomerById(customerId);
        verify(customerMapper, never()).map(any());
    }

    @Test
    void findById_WhenMapperThrowsException_ShouldPropagateException() {
        RuntimeException mapperException = new RuntimeException("Mapping failed");
        when(customerWebClient.findCustomerById(customerId)).thenReturn(customerResponseDTO);
        when(customerMapper.map(customerResponseDTO)).thenThrow(mapperException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerWebClientGateway.findById(customerId));

        assertEquals("Mapping failed", exception.getMessage());
        verify(customerWebClient, times(1)).findCustomerById(customerId);
        verify(customerMapper, times(1)).map(customerResponseDTO);
    }

    @Test
    void findById_WithNullCustomerId_ShouldHandleGracefully() {
        when(customerWebClient.findCustomerById(null)).thenReturn(customerResponseDTO);
        when(customerMapper.map(customerResponseDTO)).thenReturn(customer);

        Optional<Customer> result = customerWebClientGateway.findById(null);

        assertTrue(result.isPresent());
        verify(customerWebClient, times(1)).findCustomerById(null);
        verify(customerMapper, times(1)).map(customerResponseDTO);
    }

    @Test
    void findById_WithDifferentCustomerId_ShouldReturnCorrectCustomer() {
        UUID anotherId = UUID.randomUUID();
        CustomerResponseDTO anotherDTO = CustomerResponseDTO.builder()
                .id(anotherId)
                .fullName("Jane Smith")
                .cpf("98765432109")
                .build();
        Customer anotherCustomer = new Customer();
        anotherCustomer.setId(anotherId);
        anotherCustomer.setFullName("Jane Smith");

        when(customerWebClient.findCustomerById(anotherId)).thenReturn(anotherDTO);
        when(customerMapper.map(anotherDTO)).thenReturn(anotherCustomer);

        Optional<Customer> result = customerWebClientGateway.findById(anotherId);

        assertTrue(result.isPresent());
        assertEquals(anotherId, result.get().getId());
        assertEquals("Jane Smith", result.get().getFullName());
        verify(customerWebClient, times(1)).findCustomerById(anotherId);
        verify(customerMapper, times(1)).map(anotherDTO);
    }

    @Test
    void findById_WithMinimalCustomerData_ShouldReturnCustomer() {
        CustomerResponseDTO minimalDTO = CustomerResponseDTO.builder()
                .id(customerId)
                .build();
        Customer minimalCustomer = new Customer();
        minimalCustomer.setId(customerId);

        when(customerWebClient.findCustomerById(customerId)).thenReturn(minimalDTO);
        when(customerMapper.map(minimalDTO)).thenReturn(minimalCustomer);

        Optional<Customer> result = customerWebClientGateway.findById(customerId);

        assertTrue(result.isPresent());
        assertEquals(customerId, result.get().getId());
        assertNull(result.get().getFullName());
        assertNull(result.get().getCpf());
    }
}

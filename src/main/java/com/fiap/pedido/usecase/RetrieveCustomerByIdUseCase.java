package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Customer;
import com.fiap.pedido.exception.CustomerException;
import com.fiap.pedido.gateway.CustomerGateway;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RetrieveCustomerByIdUseCase {
    CustomerGateway customerGateway;

    public Customer execute(UUID customerId) {
        return customerGateway.findById(customerId)
                .orElseThrow(() -> new CustomerException("Failed to retrieve customer with ID: " + customerId));
    }
}

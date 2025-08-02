package com.fiap.pedido.gateway.web;

import com.fiap.pedido.domain.Customer;
import com.fiap.pedido.gateway.CustomerGateway;
import com.fiap.pedido.gateway.web.client.CustomerWebClient;
import com.fiap.pedido.mapper.CustomerMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomerWebClientGateway implements CustomerGateway {

    CustomerWebClient customerWebClient;
    CustomerMapper customerMapper;

    @Override
    public Optional<Customer> findById(UUID customerId) {
        return Optional.of(customerWebClient.findCustomerById(customerId))
                .map(customerMapper::map);
    }
}

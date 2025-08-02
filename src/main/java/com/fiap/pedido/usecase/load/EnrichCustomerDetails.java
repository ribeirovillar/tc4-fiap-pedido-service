package com.fiap.pedido.usecase.load;

import com.fiap.pedido.domain.Customer;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.usecase.RetrieveCustomerByIdUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EnrichCustomerDetails implements EnrichOrderDataStrategy {

    RetrieveCustomerByIdUseCase retrieveCustomerByIdUseCase;

    @Override
    public void enrich(Order order) {
        log.info("Loading Customer Details for Order {}", order.toString());
        Customer customer = retrieveCustomerByIdUseCase.execute(order.getCustomerId());
        order.setCustomerCpf(customer.getCpf());
        order.setCustomerName(customer.getFullName());
    }
}

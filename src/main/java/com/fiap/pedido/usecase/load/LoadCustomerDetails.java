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
public class LoadCustomerDetails implements LoadOrderDataStrategy {

    RetrieveCustomerByIdUseCase retrieveCustomerByIdUseCase;

    @Override
    public void load(Order order) {
        log.info("Loading Customer Details for Order {}", order.toString());
        try{
            Customer customer = retrieveCustomerByIdUseCase.execute(order.getCustomerId());
            order.setCustomerCpf(customer.getCpf());
            order.setCustomerName(customer.getFullName());
        } catch (Exception e){
            log.error("Error retrieving customer by ID: {}", order.getCustomerId(), e);
        }
    }
}

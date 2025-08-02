package com.fiap.pedido.gateway.web;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.gateway.PaymentGateway;
import com.fiap.pedido.gateway.web.client.PaymentWebClient;
import com.fiap.pedido.mapper.OrderMapper;
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
public class PaymentWebClientGateway implements PaymentGateway {

    PaymentWebClient paymentWebClient;
    OrderMapper orderMapper;

    @Override
    public Optional<UUID> processPayment(Order order) {
        return Optional.ofNullable(paymentWebClient.processPayment(orderMapper.mapToDto(order)));
    }
}

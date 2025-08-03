package com.fiap.pedido.gateway.database.jpa;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.gateway.OrderGateway;
import com.fiap.pedido.gateway.database.jpa.repository.OrderRepository;
import com.fiap.pedido.mapper.OrderMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderJpaGateway implements OrderGateway {

    OrderRepository orderRepository;
    OrderMapper orderMapper;

    @Override
    public Optional<Order> save(Order order) {
        return Optional.of(orderRepository.save(orderMapper.map(order)))
                .map(orderMapper::map);
    }

    @Override
    public Optional<Order> findOrderByOrderId(UUID orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(orderMapper::map);
    }

    @Override
    public Optional<Order> findOrderByPaymentId(UUID paymentId) {
        return orderRepository.findByPaymentId(paymentId).map(orderMapper::map);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll().stream().map(orderMapper::map).toList();
    }

}

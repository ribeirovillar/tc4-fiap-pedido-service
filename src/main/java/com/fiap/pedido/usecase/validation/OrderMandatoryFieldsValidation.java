package com.fiap.pedido.usecase.validation;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.exception.OrderException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
public class OrderMandatoryFieldsValidation implements ValidateOrderStrategy {
    @Override
    public void validate(Order order) {
        if (Objects.isNull(order)) {
            throw new OrderException("Order cannot be null");
        }
        if (Objects.isNull(order.getOrderId())) {
            throw new OrderException("Order ID cannot be null");
        }
        if (Objects.isNull(order.getItems()) || order.getItems().isEmpty()) {
            throw new OrderException("Order items cannot be null or empty");
        }
        if (Objects.nonNull(order.getItems().stream().filter(item -> !StringUtils.hasText(item.getSku())).findFirst().orElse(null))) {
            throw new OrderException("Order items must have a valid SKU");
        }
        if (Objects.nonNull(order.getItems().stream().filter(item -> Objects.isNull(item.getQuantity())).findFirst().orElse(null))) {
            throw new OrderException("Order items must have a valid quantity");
        }
        if (Objects.isNull(order.getCustomerId())) {
            throw new OrderException("Customer ID cannot be null");
        }
        if (!StringUtils.hasText(order.getCardNumber())) {
            throw new OrderException("Card number cannot be empty");
        }
    }
}

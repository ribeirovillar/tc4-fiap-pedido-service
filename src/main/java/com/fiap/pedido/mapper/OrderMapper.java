package com.fiap.pedido.mapper;

import com.fiap.pedido.controller.json.OrderDTO;
import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.gateway.database.jpa.entity.OrderEntity;
import com.fiap.pedido.gateway.database.jpa.entity.OrderItemEntity;
import com.fiap.pedido.gateway.web.json.PaymentDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", ignore = true)
    OrderEntity map(Order order);

    Order map(OrderEntity orderEntity);

    PaymentDTO mapToPaymentDTO(Order order);

    @Mapping(source = "orderId", target = "id")
    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "customerName", target = "customer.name")
    @Mapping(source = "customerCpf", target = "customer.cpf")
    @Mapping(source = "cardNumber", target = "customer.cardNumber")
    @Mapping(source = "paymentId", target = "payment.id")
    @Mapping(source = "paymentStatus", target = "payment.status")
    @Mapping(source = "paymentAmount", target = "payment.amount")
    OrderDTO mapToOrderDTO(Order order);

    @Mapping(target = "paymentId", source = "id")
    Order map(PaymentDTO paymentDTO);

    @AfterMapping
    default void mapItems(Order order, @MappingTarget OrderEntity orderEntity) {
        if (order.getItems() != null) {
            List<OrderItemEntity> itemEntities = new ArrayList<>();
            for (Item item : order.getItems()) {
                OrderItemEntity itemEntity = new OrderItemEntity();
                itemEntity.setSku(item.getSku());
                itemEntity.setOrderId(order.getOrderId());
                itemEntity.setName(item.getName());
                itemEntity.setQuantity(item.getQuantity());
                itemEntity.setPrice(item.getPrice());
                itemEntities.add(itemEntity);
            }
            orderEntity.setItems(itemEntities);
        }
    }
}
package com.fiap.pedido.mapper;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.gateway.database.jpa.entity.OrderEntity;
import com.fiap.pedido.gateway.database.jpa.entity.OrderItemEntity;
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
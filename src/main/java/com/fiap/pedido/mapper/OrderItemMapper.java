package com.fiap.pedido.mapper;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Product;
import com.fiap.pedido.gateway.database.jpa.entity.OrderItemEntity;
import com.fiap.pedido.gateway.web.json.ProductResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    Item map(OrderItemEntity orderItemEntity);

    OrderItemEntity map(Item item);

    Item map(Product item);

    Product map(ProductResponseDTO productResponseDTO);
}

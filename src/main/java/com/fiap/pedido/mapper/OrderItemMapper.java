package com.fiap.pedido.mapper;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Product;
import com.fiap.pedido.gateway.database.jpa.entity.OrderItemEntity;
import com.fiap.pedido.gateway.web.json.ProductResponseDTO;
import com.fiap.pedido.gateway.web.json.StockDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    Item map(OrderItemEntity orderItemEntity);

    OrderItemEntity map(Item item);

    Item map(Product item);

    Product map(ProductResponseDTO productResponseDTO);

    List<StockDTO> map(List<Item> items);

    @Mapping(source = "id", target = "productId")
    StockDTO mapToDto(Item item);

}

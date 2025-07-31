package com.fiap.pedido.gateway.web;

import com.fiap.pedido.domain.Product;
import com.fiap.pedido.gateway.ProductGateway;
import com.fiap.pedido.gateway.web.client.ProductWebClient;
import com.fiap.pedido.mapper.OrderItemMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductWebClientGateway implements ProductGateway {

    ProductWebClient productWebClient;
    OrderItemMapper orderItemMapper;

    @Override
    public List<Product> findAllProductsBySkus(List<String> skus) {
        try {
            log.info("Find all products by skus {}", skus);
            return productWebClient.findAllProductsBySkus(skus)
                    .stream()
                    .map(orderItemMapper::map)
                    .toList();
        } catch (Exception e) {
            log.error("Error retrieving products by skus: {}", skus, e);
        }
        return List.of();
    }
}

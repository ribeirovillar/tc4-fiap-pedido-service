package com.fiap.pedido.gateway;

import com.fiap.pedido.domain.Product;

import java.util.List;

public interface ProductGateway {
    List<Product> findAllProductsBySkus(List<String> skus);
}

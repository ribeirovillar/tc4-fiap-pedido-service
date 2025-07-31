package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Product;
import com.fiap.pedido.gateway.ProductGateway;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RetrieveAllProductsBySkuUseCase {
    ProductGateway productGateway;

    public List<Product> execute(List<String> skus) {
        return productGateway.findAllProductsBySkus(skus);
    }
}

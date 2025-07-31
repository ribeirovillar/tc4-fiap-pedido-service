package com.fiap.pedido.gateway.web.client;

import com.fiap.pedido.gateway.web.json.ProductResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ProductWebClient {

    @Value("${web.client.fiap-produto-service.url}")
    private String url;

    private final RestTemplate restTemplate;

    public ProductWebClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ProductResponseDTO> findAllProductsBySkus(List<String> skus) {
        StringBuilder queryParams = new StringBuilder();
        skus.forEach(sku -> queryParams.append("sku=").append(sku).append("&"));
        queryParams.deleteCharAt(queryParams.length() - 1);

        String endpoint = String.format("%s/products/skus?%s", url, queryParams);
        ProductResponseDTO[] products = restTemplate.getForObject(endpoint, ProductResponseDTO[].class);
        return List.of(products != null ? products : new ProductResponseDTO[0]);
    }
}

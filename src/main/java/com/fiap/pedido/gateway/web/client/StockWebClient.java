package com.fiap.pedido.gateway.web.client;

import com.fiap.pedido.gateway.web.json.StockDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class StockWebClient {

    @Value("${web.client.fiap-estoque-service.url}")
    private String url;

    private final RestTemplate restTemplate;

    public StockWebClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void deductStock(List<StockDTO> stocks) {
        String endpoint = String.format("%s/stocks/deduct", url);
        restTemplate.postForObject(endpoint, stocks, Void.class);
    }

    public void returnStock(List<StockDTO> stocks) {
        String endpoint = String.format("%s/stocks/reverse", url);
        restTemplate.postForObject(endpoint, stocks, Void.class);
    }

}

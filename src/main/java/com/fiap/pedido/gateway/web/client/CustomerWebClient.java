package com.fiap.pedido.gateway.web.client;

import com.fiap.pedido.gateway.web.json.CustomerResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class CustomerWebClient {

    @Value("${web.client.fiap-cliente-service.url}")
    private String url;

    private final RestTemplate restTemplate;

    public CustomerWebClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CustomerResponseDTO findCustomerById(UUID customerId) {
        String endpoint = String.format("%s/customers/%s", url, customerId);
        return restTemplate.getForObject(endpoint, CustomerResponseDTO.class);
    }
}

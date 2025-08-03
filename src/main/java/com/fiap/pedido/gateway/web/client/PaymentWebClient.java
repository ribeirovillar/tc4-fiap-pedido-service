package com.fiap.pedido.gateway.web.client;

import com.fiap.pedido.gateway.web.json.PaymentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentWebClient {

    @Value("${web.client.fiap-pagamento-service.url}")
    private String url;

    private final RestTemplate restTemplate;

    public PaymentWebClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentDTO processPayment(PaymentDTO paymentDTO) {
        return restTemplate.postForObject(url + "/payments", paymentDTO, PaymentDTO.class);
    }
}

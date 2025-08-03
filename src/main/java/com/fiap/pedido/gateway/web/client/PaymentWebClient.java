package com.fiap.pedido.gateway.web.client;

import com.fiap.pedido.domain.PaymentStatus;
import com.fiap.pedido.gateway.web.json.PaymentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@Slf4j
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

    public PaymentStatus retrievePaymentStatus(UUID paymentId) {
        log.info("Retrieving payment status for ID: {}", paymentId);

        int randomValue = new java.util.Random().nextInt(100);

        if (randomValue < 80) {
            log.info("Payment {} processed successfully", paymentId);
            return PaymentStatus.COMPLETED;
        } else {
            log.warn("Payment {} failed", paymentId);
            return PaymentStatus.FAILED;
        }
    }
}

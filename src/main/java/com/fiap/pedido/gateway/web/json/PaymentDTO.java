package com.fiap.pedido.gateway.web.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO implements Serializable {

    UUID id;
    UUID orderId;
    String customerName;
    String customerCpf;
    String cardNumber;
    BigDecimal paymentAmount;

}

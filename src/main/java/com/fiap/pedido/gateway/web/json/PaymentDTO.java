package com.fiap.pedido.gateway.web.json;

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
public class PaymentDTO implements Serializable {

    UUID orderId;
    String customerName;
    String customerCpf;
    String cardNumber;
    BigDecimal paymentAmount;

}

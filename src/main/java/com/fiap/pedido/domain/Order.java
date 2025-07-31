package com.fiap.pedido.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Order implements Serializable {
    UUID orderId;
    UUID customerId;
    String customerName;
    String customerCpf;
    String cardNumber;
    String status;
    List<Item> items;
    String paymentId;
    String paymentStatus;
    BigDecimal paymentAmount;

}

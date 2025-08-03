package com.fiap.pedido.controller.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.pedido.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDTO implements Serializable {
    UUID id;
    PaymentStatus status;
    BigDecimal amount;
}

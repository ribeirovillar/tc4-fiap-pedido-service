package com.fiap.pedido.controller.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.pedido.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO implements Serializable {

    UUID id;
    CustomerDTO customer;
    PaymentDTO payment;
    OrderStatus status;
    List<ItemDTO> items;

}

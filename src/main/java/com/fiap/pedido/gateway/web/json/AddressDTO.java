package com.fiap.pedido.gateway.web.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDTO {
    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;
}

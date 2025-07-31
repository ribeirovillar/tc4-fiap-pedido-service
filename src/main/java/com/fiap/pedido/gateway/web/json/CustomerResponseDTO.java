package com.fiap.pedido.gateway.web.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseDTO implements Serializable {
    private UUID id;
    private String fullName;
    private String cpf;
    private LocalDate birthDate;
    private AddressDTO address;
}

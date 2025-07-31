package com.fiap.pedido.gateway.web.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO implements Serializable {
    private UUID id;
    private String name;
    private String sku;
    private BigDecimal price;
}

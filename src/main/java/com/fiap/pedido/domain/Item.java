package com.fiap.pedido.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Item {
    UUID id;
    String name;
    String sku;
    Integer quantity;
    BigDecimal price;
}

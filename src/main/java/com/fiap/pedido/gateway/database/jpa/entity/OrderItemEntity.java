package com.fiap.pedido.gateway.database.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@IdClass(OrderItemId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItemEntity {

    @Id
    private String sku;
    @Id
    @Column(name = "order_id")
    private UUID orderId;
    private String name;
    private int quantity;
    private BigDecimal price;

}

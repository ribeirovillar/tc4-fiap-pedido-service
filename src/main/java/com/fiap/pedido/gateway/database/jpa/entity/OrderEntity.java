package com.fiap.pedido.gateway.database.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderEntity {

    @Id
    UUID orderId;
    UUID customerId;
    String customerName;
    String customerCpf;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    List<OrderItemEntity> items;
    String cardNumber;
    String status;
    UUID paymentId;
    String paymentStatus;
    BigDecimal paymentAmount;

}
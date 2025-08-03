package com.fiap.pedido.exception;

public class OrderStatusException extends RuntimeException {
  public OrderStatusException(String message) {
    super(message);
  }
}

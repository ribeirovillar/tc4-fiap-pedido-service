package com.fiap.pedido.exception;

public class DataEnrichmentException extends RuntimeException {
    public DataEnrichmentException(String message) {
        super(message);
    }
    public DataEnrichmentException(String message, Throwable cause) {
        super(message, cause);
    }
}

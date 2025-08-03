package com.fiap.pedido.configuration;

import com.fiap.pedido.exception.OrderNotFoundException;
import com.fiap.pedido.exception.OrderStatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderStatusException.class)
    public ResponseEntity<ProblemDetail> handleOrderStatusException(OrderStatusException ex) {
        log.error("Order status exception occurred", ex);
        ProblemDetail problemDetail = buildProblemDetail("Order Status Error", HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleOrderNotFoundException(OrderNotFoundException ex) {
        log.error("Order not found exception occurred", ex);
        ProblemDetail problemDetail = buildProblemDetail("Order Not Found", HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred", ex);
        ProblemDetail problemDetail = buildProblemDetail("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while processing your request");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred", ex);
        ProblemDetail problemDetail = buildProblemDetail("Unexpected Error", HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while processing your request");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    private ProblemDetail buildProblemDetail(String title, HttpStatus status, String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}

package com.fiap.pedido.controller;

import com.fiap.pedido.usecase.ProcessOrderPaymentUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    ProcessOrderPaymentUseCase processOrderPaymentUseCase;

    @PostMapping("{id}")
    public ResponseEntity<Void> processPayment(@PathVariable("id") UUID paymentId) {
        processOrderPaymentUseCase.execute(paymentId);
        return ResponseEntity.ok().build();
    }

}

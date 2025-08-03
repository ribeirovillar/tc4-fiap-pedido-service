package com.fiap.pedido.controller;

import com.fiap.pedido.controller.json.OrderDTO;
import com.fiap.pedido.mapper.OrderMapper;
import com.fiap.pedido.usecase.RetrieveAllOrdersUseCase;
import com.fiap.pedido.usecase.RetrieveOrderByIdUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    RetrieveAllOrdersUseCase retrieveAllOrdersUseCase;
    RetrieveOrderByIdUseCase retrieveOrderByIdUseCase;
    OrderMapper mapper;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(
                retrieveAllOrdersUseCase.execute().stream().map(mapper::mapToOrderDTO).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(mapper.mapToOrderDTO(retrieveOrderByIdUseCase.execute(id)));
    }
}

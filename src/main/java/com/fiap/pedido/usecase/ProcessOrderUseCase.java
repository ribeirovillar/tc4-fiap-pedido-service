package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.usecase.load.LoadOrderDataStrategy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProcessOrderUseCase {

    CreateOrderUseCase createOrderUseCase;
    List<LoadOrderDataStrategy> loadOrderDataStrategy;
    UpdateOrderUseCase updateOrderUseCase;

    public void execute(Order order) {
        log.info("Processing order {}", order.toString());
        createOrderUseCase.execute(order);
        loadOrderDataStrategy.forEach(loadStrategy -> loadStrategy.load(order));
        updateOrderUseCase.execute(order);

    }


}

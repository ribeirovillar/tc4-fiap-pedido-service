package com.fiap.pedido.usecase.load;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.Product;
import com.fiap.pedido.usecase.RetrieveAllProductsBySkuUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LoadProductDetails implements LoadOrderDataStrategy {

    RetrieveAllProductsBySkuUseCase retrieveAllProductsBySkuUseCase;

    @Override
    public void load(Order order) {
        log.info("Loading Product Details for Order {}", order.toString());
        List<String> skus = order.getItems().stream().map(Item::getSku).toList();
        List<Product> products = retrieveAllProductsBySkuUseCase.execute(skus);
        products.forEach(product -> order
                .getItems()
                .stream()
                .filter(item -> item.getSku().equals(product.getSku()))
                .findFirst()
                .ifPresent(item -> {
                    item.setId(product.getId());
                    item.setName(product.getName());
                    item.setPrice(product.getPrice());
                }));
        calculatePaymentAmount(order);
    }

    private void calculatePaymentAmount(Order order) {
        log.info("Calculating Payment Amount for Order {}", order.toString());
        BigDecimal totalAmount = order.getItems().stream()
                .map(item -> item.getPrice() != null
                        ? item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setPaymentAmount(totalAmount);
    }
}

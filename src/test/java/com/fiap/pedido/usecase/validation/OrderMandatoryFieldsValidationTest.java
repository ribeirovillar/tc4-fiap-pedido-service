package com.fiap.pedido.usecase.validation;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.exception.OrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderMandatoryFieldsValidationTest {

    private OrderMandatoryFieldsValidation validation;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        validation = new OrderMandatoryFieldsValidation();

        Item item1 = new Item();
        item1.setSku("SKU001");
        item1.setQuantity(2);
        item1.setPrice(BigDecimal.valueOf(50.00));

        Item item2 = new Item();
        item2.setSku("SKU002");
        item2.setQuantity(1);
        item2.setPrice(BigDecimal.valueOf(30.00));

        testOrder = new Order();
        testOrder.setOrderId(UUID.randomUUID());
        testOrder.setCustomerId(UUID.randomUUID());
        testOrder.setItems(Arrays.asList(item1, item2));
        testOrder.setCardNumber("1234-5678-9012-3456");
    }

    @Test
    void validate_WithValidOrder_ShouldNotThrowException() {
        assertDoesNotThrow(() -> validation.validate(testOrder));
    }

    @Test
    void validate_WithNullOrder_ShouldThrowOrderException() {
        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(null));

        assertEquals("Order cannot be null", exception.getMessage());
    }

    @Test
    void validate_WithNullOrderId_ShouldThrowOrderException() {
        testOrder.setOrderId(null);

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Order ID cannot be null", exception.getMessage());
    }

    @Test
    void validate_WithNullItems_ShouldThrowOrderException() {
        testOrder.setItems(null);

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Order items cannot be null or empty", exception.getMessage());
    }

    @Test
    void validate_WithEmptyItems_ShouldThrowOrderException() {
        testOrder.setItems(Collections.emptyList());

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Order items cannot be null or empty", exception.getMessage());
    }

    @Test
    void validate_WithItemHavingNullSku_ShouldThrowOrderException() {
        testOrder.getItems().getFirst().setSku(null);

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Order items must have a valid SKU", exception.getMessage());
    }

    @Test
    void validate_WithItemHavingEmptySku_ShouldThrowOrderException() {
        testOrder.getItems().getFirst().setSku("");

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Order items must have a valid SKU", exception.getMessage());
    }

    @Test
    void validate_WithItemHavingBlankSku_ShouldThrowOrderException() {
        testOrder.getItems().getFirst().setSku("   ");

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Order items must have a valid SKU", exception.getMessage());
    }

    @Test
    void validate_WithItemHavingNullQuantity_ShouldThrowOrderException() {
        testOrder.getItems().getFirst().setQuantity(null);

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Order items must have a valid quantity", exception.getMessage());
    }

    @Test
    void validate_WithNullCustomerId_ShouldThrowOrderException() {
        testOrder.setCustomerId(null);

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Customer ID cannot be null", exception.getMessage());
    }

    @Test
    void validate_WithNullCardNumber_ShouldThrowOrderException() {
        testOrder.setCardNumber(null);

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Card number cannot be empty", exception.getMessage());
    }

    @Test
    void validate_WithEmptyCardNumber_ShouldThrowOrderException() {
        testOrder.setCardNumber("");

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Card number cannot be empty", exception.getMessage());
    }

    @Test
    void validate_WithBlankCardNumber_ShouldThrowOrderException() {
        testOrder.setCardNumber("   ");

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Card number cannot be empty", exception.getMessage());
    }

    @Test
    void validate_WithSingleValidItem_ShouldNotThrowException() {
        Item singleItem = new Item();
        singleItem.setSku("SKU001");
        singleItem.setQuantity(1);
        testOrder.setItems(Collections.singletonList(singleItem));

        assertDoesNotThrow(() -> validation.validate(testOrder));
    }

    @Test
    void validate_WithZeroQuantity_ShouldNotThrowException() {
        testOrder.getItems().getFirst().setQuantity(0);

        assertDoesNotThrow(() -> validation.validate(testOrder));
    }

    @Test
    void validate_WithNegativeQuantity_ShouldNotThrowException() {
        testOrder.getItems().getFirst().setQuantity(-1);

        assertDoesNotThrow(() -> validation.validate(testOrder));
    }

    @Test
    void validate_WithMixedValidAndInvalidItems_ShouldThrowOrderException() {
        testOrder.getItems().get(1).setSku(null);

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Order items must have a valid SKU", exception.getMessage());
    }

    @Test
    void validate_WithSecondItemHavingNullQuantity_ShouldThrowOrderException() {
        testOrder.getItems().get(1).setQuantity(null);

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Order items must have a valid quantity", exception.getMessage());
    }

    @Test
    void validate_WithValidMinimalCardNumber_ShouldNotThrowException() {
        testOrder.setCardNumber("1");

        assertDoesNotThrow(() -> validation.validate(testOrder));
    }

    @Test
    void validate_ChecksFirstInvalidField() {
        testOrder.setOrderId(null);
        testOrder.setCustomerId(null);
        testOrder.setCardNumber(null);

        OrderException exception = assertThrows(OrderException.class,
                () -> validation.validate(testOrder));

        assertEquals("Order ID cannot be null", exception.getMessage());
    }
}

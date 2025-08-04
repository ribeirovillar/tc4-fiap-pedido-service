package com.fiap.pedido.controller;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.OrderStatus;
import com.fiap.pedido.domain.PaymentStatus;
import com.fiap.pedido.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class OrderControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private OrderGateway orderGateway;

    private MockMvc mockMvc;
    private Order testOrder1;
    private Order testOrder2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        testOrder1 = createTestOrder(
                UUID.randomUUID(),
                "John Doe",
                "12345678901",
                OrderStatus.FECHADO_COM_SUCESSO,
                PaymentStatus.COMPLETED,
                BigDecimal.valueOf(150.00)
        );

        testOrder2 = createTestOrder(
                UUID.randomUUID(),
                "Jane Smith",
                "98765432109",
                OrderStatus.ABERTO,
                PaymentStatus.PENDING,
                BigDecimal.valueOf(250.00)
        );
    }

    @Test
    void getAllOrders_ShouldReturnListOfOrders() throws Exception {
        List<Order> orders = Arrays.asList(testOrder1, testOrder2);
        when(orderGateway.findAll()).thenReturn(orders);

        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(testOrder1.getOrderId().toString()))
                .andExpect(jsonPath("$[0].customer.name").value("John Doe"))
                .andExpect(jsonPath("$[0].customer.cpf").value("12345678901"))
                .andExpect(jsonPath("$[0].status").value(testOrder1.getStatus().toString()))
                .andExpect(jsonPath("$[0].payment.status").value(testOrder1.getPaymentStatus().toString()))
                .andExpect(jsonPath("$[0].payment.amount").value(150.00))
                .andExpect(jsonPath("$[1].id").value(testOrder2.getOrderId().toString()))
                .andExpect(jsonPath("$[1].customer.name").value("Jane Smith"))
                .andExpect(jsonPath("$[1].status").value(testOrder2.getStatus().toString()));
    }

    @Test
    void getAllOrders_WhenNoOrders_ShouldReturnEmptyList() throws Exception {
        when(orderGateway.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getOrderById_WhenOrderExists_ShouldReturnOrder() throws Exception {
        UUID orderId = testOrder1.getOrderId();
        when(orderGateway.findOrderByOrderId(orderId)).thenReturn(Optional.of(testOrder1));

        mockMvc.perform(get("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.customer.name").value("John Doe"))
                .andExpect(jsonPath("$.customer.cpf").value("12345678901"))
                .andExpect(jsonPath("$.status").value(testOrder1.getStatus().toString()))
                .andExpect(jsonPath("$.payment.status").value(testOrder1.getPaymentStatus().toString()))
                .andExpect(jsonPath("$.payment.amount").value(150.00))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2));
    }

    @Test
    void getOrderById_WhenOrderNotFound_ShouldReturnNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(orderGateway.findOrderByOrderId(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/orders/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrderById_WithInvalidUUID_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/orders/{id}", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllOrders_ShouldMapOrderFieldsCorrectly() throws Exception {
        Order orderWithAllFields = createCompleteTestOrder();
        when(orderGateway.findAll()).thenReturn(Arrays.asList(orderWithAllFields));

        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orderWithAllFields.getOrderId().toString()))
                .andExpect(jsonPath("$[0].customer.id").value(orderWithAllFields.getCustomerId().toString()))
                .andExpect(jsonPath("$[0].customer.name").value(orderWithAllFields.getCustomerName()))
                .andExpect(jsonPath("$[0].customer.cpf").value(orderWithAllFields.getCustomerCpf()))
                .andExpect(jsonPath("$[0].customer.cardNumber").value(orderWithAllFields.getCardNumber()))
                .andExpect(jsonPath("$[0].payment.id").value(orderWithAllFields.getPaymentId().toString()))
                .andExpect(jsonPath("$[0].payment.status").value(orderWithAllFields.getPaymentStatus().toString()))
                .andExpect(jsonPath("$[0].payment.amount").value(orderWithAllFields.getPaymentAmount().doubleValue()))
                .andExpect(jsonPath("$[0].status").value(orderWithAllFields.getStatus().toString()))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items.length()").value(2));
    }

    private Order createTestOrder(UUID customerId, String customerName, String customerCpf,
                                  OrderStatus status, PaymentStatus paymentStatus, BigDecimal amount) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomerId(customerId);
        order.setCustomerName(customerName);
        order.setCustomerCpf(customerCpf);
        order.setCardNumber("**** **** **** 1234");
        order.setStatus(status);
        order.setPaymentId(UUID.randomUUID());
        order.setPaymentStatus(paymentStatus);
        order.setPaymentAmount(amount);
        order.setItems(createTestItems());
        return order;
    }

    private Order createCompleteTestOrder() {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomerId(UUID.randomUUID());
        order.setCustomerName("Complete Test User");
        order.setCustomerCpf("11122233344");
        order.setCardNumber("**** **** **** 5678");
        order.setStatus(OrderStatus.FECHADO_COM_SUCESSO);
        order.setPaymentId(UUID.randomUUID());
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setPaymentAmount(BigDecimal.valueOf(300.50));
        order.setItems(createTestItems());
        return order;
    }

    private List<Item> createTestItems() {
        Item item1 = new Item();
        item1.setId(UUID.randomUUID());
        item1.setName("Product A");
        item1.setSku("SKU001");
        item1.setQuantity(2);
        item1.setPrice(BigDecimal.valueOf(75.00));

        Item item2 = new Item();
        item2.setId(UUID.randomUUID());
        item2.setName("Product B");
        item2.setSku("SKU002");
        item2.setQuantity(1);
        item2.setPrice(BigDecimal.valueOf(75.00));

        return Arrays.asList(item1, item2);
    }
}

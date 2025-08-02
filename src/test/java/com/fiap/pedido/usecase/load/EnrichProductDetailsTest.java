package com.fiap.pedido.usecase.load;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.domain.Product;
import com.fiap.pedido.exception.ProductException;
import com.fiap.pedido.usecase.RetrieveAllProductsBySkuUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrichProductDetailsTest {

    @Mock
    private RetrieveAllProductsBySkuUseCase retrieveAllProductsBySkuUseCase;

    private EnrichProductDetails enrichProductDetails;

    private Order testOrder;
    private List<Item> testItems;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        enrichProductDetails = new EnrichProductDetails(retrieveAllProductsBySkuUseCase);

        Item item1 = new Item();
        item1.setSku("SKU001");
        item1.setQuantity(2);

        Item item2 = new Item();
        item2.setSku("SKU002");
        item2.setQuantity(1);

        testItems = Arrays.asList(item1, item2);

        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setSku("SKU001");
        product1.setName("Product 1");
        product1.setPrice(BigDecimal.valueOf(50.00));

        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setSku("SKU002");
        product2.setName("Product 2");
        product2.setPrice(BigDecimal.valueOf(30.00));

        testProducts = Arrays.asList(product1, product2);

        testOrder = new Order();
        testOrder.setOrderId(UUID.randomUUID());
        testOrder.setItems(testItems);
    }

    @Test
    void enrich_ShouldEnrichItemsWithProductDetails() {
        List<String> skus = Arrays.asList("SKU001", "SKU002");
        when(retrieveAllProductsBySkuUseCase.execute(skus)).thenReturn(testProducts);

        enrichProductDetails.enrich(testOrder);

        Item item1 = testOrder.getItems().getFirst();
        assertEquals(testProducts.getFirst().getId(), item1.getId());
        assertEquals("Product 1", item1.getName());
        assertEquals(BigDecimal.valueOf(50.00), item1.getPrice());

        Item item2 = testOrder.getItems().get(1);
        assertEquals(testProducts.get(1).getId(), item2.getId());
        assertEquals("Product 2", item2.getName());
        assertEquals(BigDecimal.valueOf(30.00), item2.getPrice());

        assertEquals(BigDecimal.valueOf(130.00), testOrder.getPaymentAmount());
        verify(retrieveAllProductsBySkuUseCase, times(1)).execute(skus);
    }

    @Test
    void enrich_WhenProductNotFound_ShouldThrowProductException() {
        List<String> skus = Arrays.asList("SKU001", "SKU002");
        List<Product> partialProducts = Collections.singletonList(testProducts.getFirst());
        when(retrieveAllProductsBySkuUseCase.execute(skus)).thenReturn(partialProducts);

        ProductException exception = assertThrows(ProductException.class,
                () -> enrichProductDetails.enrich(testOrder));

        assertTrue(exception.getMessage().contains("Invalid sku(s): SKU002"));
        verify(retrieveAllProductsBySkuUseCase, times(1)).execute(skus);
    }

    @Test
    void enrich_WithSingleItem_ShouldEnrichSuccessfully() {
        Item singleItem = testItems.getFirst();
        testOrder.setItems(Collections.singletonList(singleItem));
        List<Product> singleProduct = Collections.singletonList(testProducts.getFirst());
        when(retrieveAllProductsBySkuUseCase.execute(Collections.singletonList("SKU001"))).thenReturn(singleProduct);

        enrichProductDetails.enrich(testOrder);

        assertEquals(testProducts.getFirst().getId(), singleItem.getId());
        assertEquals("Product 1", singleItem.getName());
        assertEquals(BigDecimal.valueOf(50.00), singleItem.getPrice());
        assertEquals(BigDecimal.valueOf(100.00), testOrder.getPaymentAmount());
    }

    @Test
    void enrich_WithEmptyItems_ShouldNotThrowException() {
        testOrder.setItems(Collections.emptyList());
        when(retrieveAllProductsBySkuUseCase.execute(Collections.emptyList())).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> enrichProductDetails.enrich(testOrder));

        assertEquals(BigDecimal.ZERO, testOrder.getPaymentAmount());
    }

    @Test
    void enrich_WhenUseCaseThrowsException_ShouldPropagateException() {
        List<String> skus = Arrays.asList("SKU001", "SKU002");
        RuntimeException serviceException = new RuntimeException("Product service error");
        when(retrieveAllProductsBySkuUseCase.execute(skus)).thenThrow(serviceException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> enrichProductDetails.enrich(testOrder));

        assertEquals("Product service error", exception.getMessage());
        verify(retrieveAllProductsBySkuUseCase, times(1)).execute(skus);
    }

    @Test
    void enrich_WithNullItemPrice_ShouldCalculateWithZero() {
        testProducts.getFirst().setPrice(null);
        List<String> skus = Arrays.asList("SKU001", "SKU002");
        when(retrieveAllProductsBySkuUseCase.execute(skus)).thenReturn(testProducts);

        enrichProductDetails.enrich(testOrder);

        Item item1 = testOrder.getItems().getFirst();
        assertNull(item1.getPrice());
        assertEquals(BigDecimal.valueOf(30.00), testOrder.getPaymentAmount());
    }

    @Test
    void enrich_WithDifferentQuantities_ShouldCalculateCorrectTotal() {
        testItems.get(0).setQuantity(3);
        testItems.get(1).setQuantity(5);
        List<String> skus = Arrays.asList("SKU001", "SKU002");
        when(retrieveAllProductsBySkuUseCase.execute(skus)).thenReturn(testProducts);

        enrichProductDetails.enrich(testOrder);

        BigDecimal expectedTotal = BigDecimal.valueOf(50.00).multiply(BigDecimal.valueOf(3))
                .add(BigDecimal.valueOf(30.00).multiply(BigDecimal.valueOf(5)));
        assertEquals(expectedTotal, testOrder.getPaymentAmount());
    }

    @Test
    void enrich_WithZeroQuantity_ShouldNotAffectTotal() {
        testItems.getFirst().setQuantity(0);
        List<String> skus = Arrays.asList("SKU001", "SKU002");
        when(retrieveAllProductsBySkuUseCase.execute(skus)).thenReturn(testProducts);

        enrichProductDetails.enrich(testOrder);

        assertEquals(BigDecimal.valueOf(30.00), testOrder.getPaymentAmount());
    }

@Test
void enrich_WithMultipleSkusNotFound_ShouldListAllMissingSkus() {
    Item item3 = new Item();
    item3.setSku("SKU003");
    item3.setQuantity(1);

    // Create a new mutable list and add the new item
    List<Item> mutableItems = new ArrayList<>(testOrder.getItems());
    mutableItems.add(item3);
    testOrder.setItems(mutableItems);

    List<String> skus = Arrays.asList("SKU001", "SKU002", "SKU003");
    List<Product> singleProduct = Collections.singletonList(testProducts.getFirst());
    when(retrieveAllProductsBySkuUseCase.execute(skus)).thenReturn(singleProduct);

    ProductException exception = assertThrows(ProductException.class,
            () -> enrichProductDetails.enrich(testOrder));

    assertTrue(exception.getMessage().contains("Invalid sku(s): SKU002, SKU003"));
}

    @Test
    void enrich_WithExistingItemData_ShouldOverrideItemProperties() {
        testItems.getFirst().setId(UUID.randomUUID());
        testItems.getFirst().setName("Old Name");
        testItems.getFirst().setPrice(BigDecimal.valueOf(999.99));

        List<String> skus = Arrays.asList("SKU001", "SKU002");
        when(retrieveAllProductsBySkuUseCase.execute(skus)).thenReturn(testProducts);

        enrichProductDetails.enrich(testOrder);

        Item item1 = testOrder.getItems().getFirst();
        assertEquals(testProducts.getFirst().getId(), item1.getId());
        assertEquals("Product 1", item1.getName());
        assertEquals(BigDecimal.valueOf(50.00), item1.getPrice());
    }
}

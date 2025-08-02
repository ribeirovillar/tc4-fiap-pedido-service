package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Product;
import com.fiap.pedido.gateway.ProductGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetrieveAllProductsBySkuUseCaseTest {

    @Mock
    private ProductGateway productGateway;

    private RetrieveAllProductsBySkuUseCase retrieveAllProductsBySkuUseCase;

    private List<String> testSkus;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        retrieveAllProductsBySkuUseCase = new RetrieveAllProductsBySkuUseCase(productGateway);

        testSkus = Arrays.asList("SKU001", "SKU002", "SKU003");

        Product product1 = new Product();
        product1.setSku("SKU001");
        product1.setName("Product 1");
        product1.setPrice(BigDecimal.valueOf(50.00));

        Product product2 = new Product();
        product2.setSku("SKU002");
        product2.setName("Product 2");
        product2.setPrice(BigDecimal.valueOf(75.00));

        Product product3 = new Product();
        product3.setSku("SKU003");
        product3.setName("Product 3");
        product3.setPrice(BigDecimal.valueOf(100.00));

        testProducts = Arrays.asList(product1, product2, product3);
    }

    @Test
    void execute_ShouldReturnAllProductsForGivenSkus() {
        when(productGateway.findAllProductsBySkus(testSkus)).thenReturn(testProducts);

        List<Product> result = retrieveAllProductsBySkuUseCase.execute(testSkus);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(testProducts, result);
        verify(productGateway, times(1)).findAllProductsBySkus(testSkus);
    }

    @Test
    void execute_WithEmptySkuList_ShouldReturnEmptyList() {
        List<String> emptySkus = Collections.emptyList();
        when(productGateway.findAllProductsBySkus(emptySkus)).thenReturn(Collections.emptyList());

        List<Product> result = retrieveAllProductsBySkuUseCase.execute(emptySkus);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productGateway, times(1)).findAllProductsBySkus(emptySkus);
    }

    @Test
    void execute_WithSingleSku_ShouldReturnSingleProduct() {
        List<String> singleSku = Collections.singletonList("SKU001");
        List<Product> singleProduct = Collections.singletonList(testProducts.getFirst());
        when(productGateway.findAllProductsBySkus(singleSku)).thenReturn(singleProduct);

        List<Product> result = retrieveAllProductsBySkuUseCase.execute(singleSku);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SKU001", result.getFirst().getSku());
        assertEquals("Product 1", result.getFirst().getName());
        verify(productGateway, times(1)).findAllProductsBySkus(singleSku);
    }

    @Test
    void execute_WhenSomeSkusNotFound_ShouldReturnPartialResults() {
        List<Product> partialProducts = Arrays.asList(testProducts.get(0), testProducts.get(1));
        when(productGateway.findAllProductsBySkus(testSkus)).thenReturn(partialProducts);

        List<Product> result = retrieveAllProductsBySkuUseCase.execute(testSkus);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productGateway, times(1)).findAllProductsBySkus(testSkus);
    }

    @Test
    void execute_WhenNoProductsFound_ShouldReturnEmptyList() {
        when(productGateway.findAllProductsBySkus(testSkus)).thenReturn(Collections.emptyList());

        List<Product> result = retrieveAllProductsBySkuUseCase.execute(testSkus);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productGateway, times(1)).findAllProductsBySkus(testSkus);
    }

    @Test
    void execute_WithNullSkuList_ShouldDelegateToGateway() {
        when(productGateway.findAllProductsBySkus(null)).thenReturn(Collections.emptyList());

        List<Product> result = retrieveAllProductsBySkuUseCase.execute(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productGateway, times(1)).findAllProductsBySkus(null);
    }

    @Test
    void execute_WhenGatewayThrowsException_ShouldPropagateException() {
        RuntimeException gatewayException = new RuntimeException("Database connection error");
        when(productGateway.findAllProductsBySkus(testSkus)).thenThrow(gatewayException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> retrieveAllProductsBySkuUseCase.execute(testSkus));

        assertEquals("Database connection error", exception.getMessage());
        verify(productGateway, times(1)).findAllProductsBySkus(testSkus);
    }

    @Test
    void execute_WithDuplicateSkus_ShouldHandleCorrectly() {
        List<String> duplicateSkus = Arrays.asList("SKU001", "SKU001", "SKU002");
        when(productGateway.findAllProductsBySkus(duplicateSkus)).thenReturn(testProducts.subList(0, 2));

        List<Product> result = retrieveAllProductsBySkuUseCase.execute(duplicateSkus);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productGateway, times(1)).findAllProductsBySkus(duplicateSkus);
    }
}

package com.fiap.pedido.gateway.web;

import com.fiap.pedido.domain.Product;
import com.fiap.pedido.gateway.web.client.ProductWebClient;
import com.fiap.pedido.gateway.web.json.ProductResponseDTO;
import com.fiap.pedido.mapper.OrderItemMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductWebClientGatewayTest {

    @Mock
    private ProductWebClient productWebClient;

    @Mock
    private OrderItemMapper orderItemMapper;

    private ProductWebClientGateway productWebClientGateway;

    private List<String> testSkus;
    private List<ProductResponseDTO> testProductDTOs;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        productWebClientGateway = new ProductWebClientGateway(productWebClient, orderItemMapper);

        testSkus = Arrays.asList("SKU001", "SKU002", "SKU003");

        ProductResponseDTO dto1 = new ProductResponseDTO(UUID.randomUUID(), "Product 1", "SKU001", BigDecimal.valueOf(50.00));
        ProductResponseDTO dto2 = new ProductResponseDTO(UUID.randomUUID(), "Product 2", "SKU002", BigDecimal.valueOf(75.00));
        ProductResponseDTO dto3 = new ProductResponseDTO(UUID.randomUUID(), "Product 3", "SKU003", BigDecimal.valueOf(100.00));
        testProductDTOs = Arrays.asList(dto1, dto2, dto3);

        Product product1 = new Product();
        product1.setId(dto1.getId());
        product1.setName("Product 1");
        product1.setSku("SKU001");
        product1.setPrice(BigDecimal.valueOf(50.00));

        Product product2 = new Product();
        product2.setId(dto2.getId());
        product2.setName("Product 2");
        product2.setSku("SKU002");
        product2.setPrice(BigDecimal.valueOf(75.00));

        Product product3 = new Product();
        product3.setId(dto3.getId());
        product3.setName("Product 3");
        product3.setSku("SKU003");
        product3.setPrice(BigDecimal.valueOf(100.00));

        testProducts = Arrays.asList(product1, product2, product3);
    }

    @Test
    void findAllProductsBySkus_ShouldReturnMappedProducts() {
        when(productWebClient.findAllProductsBySkus(testSkus)).thenReturn(testProductDTOs);
        when(orderItemMapper.map(testProductDTOs.get(0))).thenReturn(testProducts.get(0));
        when(orderItemMapper.map(testProductDTOs.get(1))).thenReturn(testProducts.get(1));
        when(orderItemMapper.map(testProductDTOs.get(2))).thenReturn(testProducts.get(2));

        List<Product> result = productWebClientGateway.findAllProductsBySkus(testSkus);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(testProducts, result);
        verify(productWebClient, times(1)).findAllProductsBySkus(testSkus);
        verify(orderItemMapper, times(1)).map(testProductDTOs.get(0));
        verify(orderItemMapper, times(1)).map(testProductDTOs.get(1));
        verify(orderItemMapper, times(1)).map(testProductDTOs.get(2));
    }

    @Test
    void findAllProductsBySkus_WithEmptySkuList_ShouldReturnEmptyList() {
        List<String> emptySkus = Collections.emptyList();
        when(productWebClient.findAllProductsBySkus(emptySkus)).thenReturn(Collections.emptyList());

        List<Product> result = productWebClientGateway.findAllProductsBySkus(emptySkus);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productWebClient, times(1)).findAllProductsBySkus(emptySkus);
        verify(orderItemMapper, never()).map(any(ProductResponseDTO.class));
    }

    @Test
    void findAllProductsBySkus_WithSingleSku_ShouldReturnSingleProduct() {
        List<String> singleSku = Collections.singletonList("SKU001");
        List<ProductResponseDTO> singleDTO = Collections.singletonList(testProductDTOs.getFirst());
        when(productWebClient.findAllProductsBySkus(singleSku)).thenReturn(singleDTO);
        when(orderItemMapper.map(testProductDTOs.getFirst())).thenReturn(testProducts.getFirst());

        List<Product> result = productWebClientGateway.findAllProductsBySkus(singleSku);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProducts.getFirst(), result.getFirst());
        verify(productWebClient, times(1)).findAllProductsBySkus(singleSku);
        verify(orderItemMapper, times(1)).map(testProductDTOs.getFirst());
    }

    @Test
    void findAllProductsBySkus_WhenWebClientThrowsException_ShouldPropagateException() {
        RuntimeException webClientException = new RuntimeException("Product service error");
        when(productWebClient.findAllProductsBySkus(testSkus)).thenThrow(webClientException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productWebClientGateway.findAllProductsBySkus(testSkus));

        assertEquals("Product service error", exception.getMessage());
        verify(productWebClient, times(1)).findAllProductsBySkus(testSkus);
        verify(orderItemMapper, never()).map(any(ProductResponseDTO.class));
    }

    @Test
    void findAllProductsBySkus_WhenMapperThrowsException_ShouldPropagateException() {
        RuntimeException mapperException = new RuntimeException("Mapping failed");
        when(productWebClient.findAllProductsBySkus(testSkus)).thenReturn(testProductDTOs);
        when(orderItemMapper.map(testProductDTOs.getFirst())).thenThrow(mapperException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productWebClientGateway.findAllProductsBySkus(testSkus));

        assertEquals("Mapping failed", exception.getMessage());
        verify(productWebClient, times(1)).findAllProductsBySkus(testSkus);
        verify(orderItemMapper, times(1)).map(testProductDTOs.getFirst());
    }

    @Test
    void findAllProductsBySkus_WithNullSkuList_ShouldHandleGracefully() {
        when(productWebClient.findAllProductsBySkus(null)).thenReturn(Collections.emptyList());

        List<Product> result = productWebClientGateway.findAllProductsBySkus(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productWebClient, times(1)).findAllProductsBySkus(null);
    }

    @Test
    void findAllProductsBySkus_WithPartialResults_ShouldMapAvailableProducts() {
        List<ProductResponseDTO> partialDTOs = Arrays.asList(testProductDTOs.get(0), testProductDTOs.get(1));
        when(productWebClient.findAllProductsBySkus(testSkus)).thenReturn(partialDTOs);
        when(orderItemMapper.map(testProductDTOs.get(0))).thenReturn(testProducts.get(0));
        when(orderItemMapper.map(testProductDTOs.get(1))).thenReturn(testProducts.get(1));

        List<Product> result = productWebClientGateway.findAllProductsBySkus(testSkus);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productWebClient, times(1)).findAllProductsBySkus(testSkus);
        verify(orderItemMapper, times(1)).map(testProductDTOs.get(0));
        verify(orderItemMapper, times(1)).map(testProductDTOs.get(1));
        verify(orderItemMapper, never()).map(testProductDTOs.get(2));
    }

    @Test
    void findAllProductsBySkus_WithNullProductInResponse_ShouldHandleGracefully() {
        when(productWebClient.findAllProductsBySkus(testSkus)).thenReturn(testProductDTOs);
        when(orderItemMapper.map(testProductDTOs.get(0))).thenReturn(testProducts.get(0));
        when(orderItemMapper.map(testProductDTOs.get(1))).thenReturn(null);
        when(orderItemMapper.map(testProductDTOs.get(2))).thenReturn(testProducts.get(2));

        List<Product> result = productWebClientGateway.findAllProductsBySkus(testSkus);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(testProducts.get(0), result.get(0));
        assertNull(result.get(1));
        assertEquals(testProducts.get(2), result.get(2));
    }
}

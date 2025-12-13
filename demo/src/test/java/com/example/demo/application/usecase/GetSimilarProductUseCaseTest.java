package com.example.demo.application.usecase;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.application.port.ProductDetailPort;
import com.example.demo.application.port.SimilarIdsPort;
import com.example.demo.domain.model.ProductDetail;
import com.example.demo.shared.exception.ProductNotFoundException;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GetSimilarProductUseCaseTest {

    @Mock
    SimilarIdsPort similarIdsPort;
    @Mock
    ProductDetailPort productDetailPort;

    private final Executor thread = Runnable::run;

    @Test
    void shouldReturnDetailsSkippingFail() {
        //given
        when(similarIdsPort.getSimilarIds(1L)).thenReturn(List.of(2L, 3L, 4L));

        when(productDetailPort.getProductDetail(2L)).thenReturn(product(2L, "P2"));
        when(productDetailPort.getProductDetail(3L)).thenThrow(new RuntimeException("boom")); // fallo -> se omite
        when(productDetailPort.getProductDetail(4L)).thenReturn(product(4L, "P4"));

        GetSimilarProductsUseCase useCase = new GetSimilarProductsUseCase(similarIdsPort, productDetailPort,
                thread);

        // when
        List<ProductDetail> result = useCase.getSimilarProducts(1L);

        // then
        assertThat(result).extracting(ProductDetail::getProductId).containsExactly(2L, 4L);
        verify(similarIdsPort).getSimilarIds(1L);
        verify(productDetailPort).getProductDetail(2L);
        verify(productDetailPort).getProductDetail(3L);
        verify(productDetailPort).getProductDetail(4L);
    }

    @Test
    void ShouldPropagateNotFoundFromSimilarIds() {
        when(similarIdsPort.getSimilarIds(5L)).thenThrow(new ProductNotFoundException("Product 5 not found"));

        GetSimilarProductsUseCase useCase = new GetSimilarProductsUseCase(similarIdsPort, productDetailPort,
                thread);

        assertThatThrownBy(() -> useCase.getSimilarProducts(5L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    private static ProductDetail product(long id, String name) {
        return ProductDetail.builder()
                .productId(id)
                .name(name)
                .price(new BigDecimal("10.00"))
                .availability(true)
                .build();
    }
}

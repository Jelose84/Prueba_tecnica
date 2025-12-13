package com.example.application.usecase;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import com.example.application.port.ProductDetailPort;
import com.example.application.port.SimilarIdsPort;
import com.example.demo.domain.model.ProductDetail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetSimilarProductsUseCase {

  private final SimilarIdsPort similarIdsPort;
  private final ProductDetailPort productDetailPort;
  private final Executor downstreamExecutor;

  public List<ProductDetail> getSimilarProducts(long productId) {
    List<Long> ids = similarIdsPort.getSimilarIds(productId);

    // Mantiene el orden de similarids al hacer join en el mismo orden
    List<CompletableFuture<Optional<ProductDetail>>> futures = ids.stream()
        .map(id -> CompletableFuture.supplyAsync(() -> fetchSafe(id), downstreamExecutor))
        .toList();

    return futures.stream()
        .map(CompletableFuture::join)
        .flatMap(Optional::stream)
        .toList();
  }

  private Optional<ProductDetail> fetchSafe(long id) {
    try {
      return Optional.of(productDetailPort.getProductDetail(id));
    } catch (Exception ignored) {
      // resiliencia simple: si un similar falla, lo omitimos
      return Optional.empty();
    }
  }
}

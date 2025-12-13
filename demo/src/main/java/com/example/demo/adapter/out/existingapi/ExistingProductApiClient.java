package com.example.demo.adapter.out.existingapi;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.adapter.out.existingapi.dto.ExternalProductDetailDto;
import com.example.demo.adapter.out.existingapi.mapper.ExternalProductMapper;
import com.example.demo.application.port.ProductDetailPort;
import com.example.demo.application.port.SimilarIdsPort;
import com.example.demo.domain.model.ProductDetail;
import com.example.demo.shared.exception.DownstreamException;
import com.example.demo.shared.exception.ProductNotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExistingProductApiClient implements SimilarIdsPort, ProductDetailPort {

  private final RestTemplate restTemplate;
  private final ExistingApiProperties props;
  private final ExternalProductMapper mapper;

  @Override
  public List<Long> getSimilarIds(long productId) {
    try {
      ResponseEntity<Long[]> response = restTemplate.getForEntity(
          props.baseUrl() + "/product/{productId}/similarids",
          Long[].class,
          productId
      );
      Long[] body = response.getBody();
      return body == null ? List.of() : Arrays.asList(body);

    } catch (HttpClientErrorException.NotFound e) {
      throw new ProductNotFoundException("Product " + productId + " not found");
    } catch (Exception e) {
      throw new DownstreamException("Downstream error calling similarids", e);
    }
  }

  @Override
  public ProductDetail getProductDetail(long productId) {
    try {
      ExternalProductDetailDto dto = restTemplate.getForObject(
          props.baseUrl() + "/product/{productId}",
          ExternalProductDetailDto.class,
          productId
      );
      if (dto == null) {
        throw new DownstreamException("Downstream returned null body for product " + productId);
      }
      return mapper.toDomain(dto);

    } catch (HttpClientErrorException.NotFound e) {
      // Para "similars": lo dejamos fallar y el use case lo omitirá
      throw new ProductNotFoundException("Similar product " + productId + " not found");
    } catch (Exception e) {
      throw new DownstreamException("Downstream error calling product detail", e);
    }
  }
}
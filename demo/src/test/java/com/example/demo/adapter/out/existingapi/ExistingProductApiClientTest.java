package com.example.demo.adapter.out.existingapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import com.example.demo.adapter.out.existingapi.mapper.ExternalProductMapper;
import com.example.demo.domain.model.ProductDetail;
import com.example.demo.shared.exception.ProductNotFoundException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

class ExistingProductApiClientIT {

  static MockWebServer server;

  ExistingProductApiClient client;
    static {
        server = new MockWebServer();
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
  @BeforeAll
  static void startServer() throws IOException {
    server = new MockWebServer();
    server.start();
  }

  @AfterAll
  static void stopServer() throws IOException {
    server.shutdown();
  }

  @BeforeEach
  void setUp() {
    String baseUrl = server.url("").toString().replaceAll("/$", "");

    ExistingApiProperties props = new ExistingApiProperties(
        baseUrl,
        200,   // connectTimeoutMs
        500,   // readTimeoutMs
        4,      // parallelism (si aplica)
        200
    );

    RestTemplate restTemplate = new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofMillis(props.connectTimeoutMs()))
        .setReadTimeout(Duration.ofMillis(props.readTimeoutMs()))
        .build();

    ExternalProductMapper mapper = Mappers.getMapper(ExternalProductMapper.class);

    client = new ExistingProductApiClient(restTemplate, props, mapper);
  }

  @Test
  void getSimilarIds_shouldReturnList() throws Exception {
    server.enqueue(jsonResponse("[2,3]"));

    List<Long> ids = client.getSimilarIds(1L);

    assertThat(ids).containsExactly(2L, 3L);

    RecordedRequest req = server.takeRequest(2, TimeUnit.SECONDS);
    assertThat(req).isNotNull();
    assertThat(req.getPath()).isEqualTo("/product/1/similarids");
  }

  @Test
  void getSimilarIds_shouldThrowNotFoundOn404() {
    server.enqueue(new MockResponse().setResponseCode(404));

    assertThatThrownBy(() -> client.getSimilarIds(999L))
        .isInstanceOf(ProductNotFoundException.class);
  }

  @Test
  void getProductDetail_shouldMapDtoToDomain() throws Exception {
    server.enqueue(jsonResponse("""
      {"id":2,"name":"Dress","price":19.99,"availability":true}
    """));

    ProductDetail detail = client.getProductDetail(2L);

    assertThat(detail.getProductId()).isEqualTo(2L);
    assertThat(detail.getName()).isEqualTo("Dress");

    RecordedRequest req = server.takeRequest(2, TimeUnit.SECONDS);
    assertThat(req).isNotNull();
    assertThat(req.getPath()).isEqualTo("/product/2");
  }

  private static MockResponse jsonResponse(String body) {
    return new MockResponse()
        .setResponseCode(200)
        .addHeader("Content-Type", "application/json")
        .setBody(body);
  }
}

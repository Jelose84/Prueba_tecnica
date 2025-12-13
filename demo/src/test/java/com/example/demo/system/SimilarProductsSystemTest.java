package com.example.demo.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.example.demo.adapter.out.existingapi.ExistingProductApiClient;
import com.example.demo.domain.model.ProductDetail;
import com.example.demo.shared.exception.ProductNotFoundException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimilarProductsSystemTest {

    static MockWebServer server;

    @Autowired
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
    static void beforeAll() throws Exception {
        server = new MockWebServer();
        server.start();
    }

    @AfterAll
    static void afterAll() throws Exception {
        server.shutdown();
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        String baseUrl = server.url("").toString().replaceAll("/$", "");
        registry.add("existing-api.base-url", () -> baseUrl);
        registry.add("existing-api.connect-timeout-ms", () -> "200");
        registry.add("existing-api.read-timeout-ms", () -> "500");
        registry.add("existing-api.parallelism", () -> "4");
    }

    @Test
    void getSimilarIds_shouldReturnList() throws Exception {
        server.enqueue(jsonResponse("[2,3]"));

        List<Long> ids = client.getSimilarIds(1L);

        assertThat(ids).containsExactly(2L, 3L);

        RecordedRequest req = server.takeRequest(1, TimeUnit.SECONDS);
        assertThat(req.getPath()).isEqualTo("/product/1/similarids");
    }

    @Test
    void getSimilarIds_shouldThrowNotFoundOn404() {
        server.enqueue(new MockResponse().setResponseCode(404));

        assertThatThrownBy(() -> client.getSimilarIds(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void getProductDetail_shouldMapExternalDtoToDomain() throws Exception {
        server.enqueue(jsonResponse("""
                  {"id":2,"name":"Dress","price":19.99,"availability":true}
                """));

        ProductDetail detail = client.getProductDetail(2L);

        assertThat(detail.getProductId()).isEqualTo(2L);
        assertThat(detail.getName()).isEqualTo("Dress");

        RecordedRequest req = server.takeRequest(1, TimeUnit.SECONDS);
        assertThat(req.getPath()).isEqualTo("/product/2");
    }

    private static MockResponse jsonResponse(String body) {
        return new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(body);
    }
}

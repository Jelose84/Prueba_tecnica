package com.example.demo.config;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.example.demo.adapter.out.existingapi.ExistingApiProperties;

@Configuration
@EnableConfigurationProperties(ExistingApiProperties.class)
public class AppConfig {

  @Bean(destroyMethod = "shutdown")
  public ExecutorService productDetailExecutor(ExistingApiProperties props) {
    int threads = props.parallelism();

    int queueCapacity = props.queueCapacity();
    BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueCapacity);

    AtomicInteger n = new AtomicInteger(1);

    ThreadFactory tf = r -> {
      Thread t = new Thread(r);
      t.setName("product-detail-" + n.getAndIncrement());
      t.setDaemon(true);
      return t;
    };

    return new ThreadPoolExecutor(
        threads,
        threads,
        0L, TimeUnit.MILLISECONDS,
        queue,
        tf,
        // Backpressure: si está lleno, el hilo que llama ejecuta la tarea (reduce QPS y evita OOM)
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder b, ExistingApiProperties props) {
    return b
      .setConnectTimeout(Duration.ofMillis(props.connectTimeoutMs()))
      .setReadTimeout(Duration.ofMillis(props.readTimeoutMs()))
      .build();
  }
}

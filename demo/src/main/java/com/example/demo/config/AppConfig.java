package com.example.demo.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.example.demo.adapter.out.existingapi.ExistingApiProperties;

@Configuration
@EnableConfigurationProperties(ExistingApiProperties.class)
public class AppConfig {

  @Bean
  public RestTemplate restTemplate(ExistingApiProperties props) {
    SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
    f.setConnectTimeout(props.connectTimeoutMs());
    f.setReadTimeout(props.readTimeoutMs());
    return new RestTemplate(f);
  }

  @Bean
  public Executor downstreamExecutor(ExistingApiProperties props) {
    return Executors.newFixedThreadPool(Math.max(1, props.parallelism()));
  }
}
package com.example.demo.domain.model;

import java.math.BigDecimal;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
@RequiredArgsConstructor
public class ProductDetail {
  Long productId;
  String name;
  BigDecimal price;
  boolean availability;
}
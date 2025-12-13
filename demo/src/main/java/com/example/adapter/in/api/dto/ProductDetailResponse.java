package com.example.adapter.in.api.dto;

import java.math.BigDecimal;

public record ProductDetailResponse(
    Long productId,
    String name,
    BigDecimal price,
    boolean availability
) {}
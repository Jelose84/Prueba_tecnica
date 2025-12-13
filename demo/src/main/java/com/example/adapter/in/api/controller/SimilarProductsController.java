package com.example.adapter.in.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.adapter.in.api.dto.ProductDetailResponse;
import com.example.adapter.in.api.mapper.ApiProductMapper;
import com.example.application.usecase.GetSimilarProductsUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class SimilarProductsController {

  private final GetSimilarProductsUseCase useCase;
  private final ApiProductMapper mapper;

  @GetMapping("/{productId}/similar")
  public List<ProductDetailResponse> getSimilar(@PathVariable long productId) {
    return useCase.getSimilarProducts(productId).stream()
        .map(mapper::toResponse)
        .toList();
  }
}
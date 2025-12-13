package com.example.adapter.in.api.mapper;

import org.mapstruct.Mapper;

import com.example.adapter.in.api.dto.ProductDetailResponse;
import com.example.demo.domain.model.ProductDetail;

@Mapper(componentModel = "spring")
public interface ApiProductMapper {
  ProductDetailResponse toResponse(ProductDetail domain);
}
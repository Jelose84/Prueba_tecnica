package com.example.demo.adapter.out.existingapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.adapter.out.existingapi.dto.ExternalProductDetailDto;
import com.example.demo.domain.model.ProductDetail;

@Mapper(componentModel = "spring")
public interface ExternalProductMapper {

  @Mapping(target = "productId", source = "id")
  @Mapping(target = "availability", expression = "java(Boolean.TRUE.equals(dto.getAvailability()))")
  ProductDetail toDomain(ExternalProductDetailDto dto);
}
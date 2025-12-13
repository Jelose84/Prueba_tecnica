package com.example.demo.adapter.out.existingapi.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalProductDetailDto {
  private Long id;
  private String name;
  private BigDecimal price;
  private Boolean availability;
}